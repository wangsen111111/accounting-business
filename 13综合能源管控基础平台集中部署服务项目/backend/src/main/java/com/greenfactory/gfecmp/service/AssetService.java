package com.greenfactory.gfecmp.service;

import com.greenfactory.gfecmp.dto.AssetView;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * 碳资产履约预测（FR-006）：配额 + CCER - 累计排放 = 预计结余。
 */
@Service
public class AssetService {

    private static final double QUOTA = 12_000d;
    private static final double CCER = 1_800d;
    private static final double SAFE_BALANCE = 2_500d;
    private static final DateTimeFormatter MONTH_DAY = DateTimeFormatter.ofPattern("MM-dd");

    private final EnergyDataService energyDataService;

    public AssetService(EnergyDataService energyDataService) {
        this.energyDataService = energyDataService;
    }

    public AssetView build() {
        double dayCarbon = Math.max(energyDataService.fullDayCarbon(), 0.01d);
        // 按当前日均排放推算年度累计排放（演示口径）
        double emitted = DashboardService.round(dayCarbon * 620d, 2);
        double totalAsset = QUOTA + CCER;
        double balance = DashboardService.round(totalAsset - emitted, 2);
        double progress = DashboardService.round(Math.min(1d, emitted / totalAsset) * 100d, 1);

        String riskLevel;
        String riskText;
        if (balance < 0) {
            riskLevel = "danger";
            riskText = "预计结余为负，需立即购入配额";
        } else if (balance < SAFE_BALANCE) {
            riskLevel = "warning";
            riskText = "预计结余低于安全阈值，建议提前安排交易";
        } else {
            riskLevel = "normal";
            riskText = "预计结余充足，履约风险可控";
        }

        double price = marketPrice(0);
        AssetView.Kpi kpi = new AssetView.Kpi(QUOTA, CCER, totalAsset, emitted, balance,
                price, DashboardService.round(balance * price, 2), progress, riskLevel, riskText);

        List<AssetView.AssetItem> assets = List.of(
                new AssetView.AssetItem("CEA", "全国碳配额", QUOTA, "tCO₂e", "2024 履约周期", price, "已登记"),
                new AssetView.AssetItem("CCER", "国家核证自愿减排量", CCER, "tCO₂e", "2024 履约周期", price * 0.92, "已登记"),
                new AssetView.AssetItem("CERS", "地方试点碳配额", 0, "tCO₂e", "—", 0, "未持有"));

        List<AssetView.Plan> plans = List.of(
                new AssetView.Plan("空压机群控改造", "设备动力部", "2025-03-31", 186.4, "实施中"),
                new AssetView.Plan("窑炉余热回收", "生产制造部", "2025-05-30", 342.8, "方案评审"),
                new AssetView.Plan("厂区屋顶光伏（2.4MW）", "能源管理部", "2025-06-30", 512.6, "招标中"),
                new AssetView.Plan("照明 LED 替换", "设备动力部", "2024-12-31", 48.2, "已完成"));

        return new AssetView(kpi, assets, buildMarket(), buildRing(emitted, balance), plans);
    }

    private AssetView.Market buildMarket() {
        List<String> labels = new ArrayList<>();
        List<Double> prices = new ArrayList<>();
        LocalDate start = LocalDate.now().minusDays(29);
        for (int i = 0; i < 30; i++) {
            labels.add(start.plusDays(i).format(MONTH_DAY));
            prices.add(marketPrice(i));
        }
        double latest = prices.get(prices.size() - 1);
        double previous = prices.get(prices.size() - 2);
        return new AssetView.Market(labels, prices, latest,
                DashboardService.round(latest - previous, 2),
                prices.stream().mapToDouble(Double::doubleValue).max().orElse(0),
                prices.stream().mapToDouble(Double::doubleValue).min().orElse(0));
    }

    /** 确定性的行情曲线：基准价 + 正弦波动 + 小幅噪声 */
    private double marketPrice(int dayOffset) {
        Random random = new Random(9527L + dayOffset);
        double base = 0.0088d * dayOffset * dayOffset / 6d;
        double wave = Math.sin(dayOffset / 3.4d) * 1.9d;
        double noise = (random.nextDouble() - 0.5) * 0.9d;
        return DashboardService.round(68.4d + base + wave + noise, 2);
    }

    private List<AssetView.RingItem> buildRing(double emitted, double balance) {
        return List.of(
                new AssetView.RingItem("碳配额 CEA", QUOTA, "#2F9E63"),
                new AssetView.RingItem("CCER", CCER, "#F2A93B"),
                new AssetView.RingItem("累计排放", emitted, "#7C8B85"));
    }
}
