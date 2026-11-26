package com.greenfactory.gfecmp.service;

import com.greenfactory.gfecmp.dto.AccountingView;
import com.greenfactory.gfecmp.dto.AlertView;
import com.greenfactory.gfecmp.dto.FactorView;
import com.greenfactory.gfecmp.model.EnergyType;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 碳排放核算 / 因子库 / 趋势预警。
 */
@Service
public class CarbonService {

    private static final String[] MONTHS = {
            "2025-01", "2025-02", "2025-03", "2025-04", "2025-05", "2025-06",
            "2025-07", "2025-08", "2025-09", "2025-10", "2025-11", "2025-12"
    };
    private static final double[] MONTH_FACTOR = {
            0.86, 0.78, 0.93, 0.97, 1.02, 1.08, 1.19, 1.22, 1.05, 0.98, 0.95, 0.90
    };
    /** 年度配额（tCO₂e，演示值） */
    private static final double ANNUAL_QUOTA = 12_000d;

    private final EnergyDataService energyDataService;

    public CarbonService(EnergyDataService energyDataService) {
        this.energyDataService = energyDataService;
    }

    /** 碳因子库（演示数据，字段对应 FR-003） */
    public List<FactorView> factors() {
        List<FactorView> list = new ArrayList<>();
        list.add(new FactorView("F-ELEC-001", "华东区域电网电力", "排放源", "排放因子",
                0.5703, "kgCO₂/kWh", "生态环境部《企业温室气体排放核算指南》", "2025-01-01", 3));
        list.add(new FactorView("F-GAS-001", "天然气", "排放源", "排放因子",
                2.1620, "kgCO₂/m³", "省级温室气体清单编制指南", "2025-01-01", 2));
        list.add(new FactorView("F-COAL-001", "原煤", "排放源", "排放因子",
                1.9003, "kgCO₂/kg", "GB/T 32150-2015", "2025-01-01", 4));
        list.add(new FactorView("F-GASO-001", "汽油", "排放源", "排放因子",
                2.9250, "kgCO₂/L", "GB/T 32151.5-2015", "2025-01-01", 1));
        list.add(new FactorView("F-DIES-001", "柴油", "排放源", "排放因子",
                3.0960, "kgCO₂/L", "GB/T 32151.5-2015", "2025-01-01", 1));
        list.add(new FactorView("F-WATR-001", "自来水", "排放源", "排放因子",
                0.3440, "kgCO₂/m³", "中国产品全生命周期温室气体排放系数库", "2025-01-01", 2));
        list.add(new FactorView("F-STEM-001", "外购蒸汽", "排放源", "排放因子",
                0.1100, "kgCO₂/kg", "企业实测值（2024 年度）", "2025-01-01", 1));
        list.add(new FactorView("F-GELE-001", "绿电（风电/光伏）", "减排量", "排放因子",
                0.5703, "kgCO₂/kWh", "绿证核销对应电网因子抵扣", "2025-01-01", 1));
        list.add(new FactorView("F-TREE-001", "厂区绿化碳汇", "吸收量", "活动水平",
                0.0120, "tCO₂/株·年", "林业碳汇计量监测技术规程", "2025-01-01", 1));
        return list;
    }

    /** 年度碳排放核算：月度汇总 + 排放源明细 */
    public AccountingView accounting(int year) {
        double dayCarbon = Math.max(energyDataService.fullDayCarbon(), 0.01d);
        double dayTce = Math.max(energyDataService.fullDayTce(), 0.01d);
        Random random = new Random(year * 31L + 7);

        List<AccountingView.MonthRow> months = new ArrayList<>();
        double totalEmission = 0d;
        double totalTce = 0d;
        double prev = 0d;
        for (int i = 0; i < MONTHS.length; i++) {
            double emission = dayCarbon * 30 * MONTH_FACTOR[i] * (0.97 + random.nextDouble() * 0.06);
            double tce = dayTce * 30 * MONTH_FACTOR[i] * (0.97 + random.nextDouble() * 0.06);
            double intensity = emission * 1000d / Math.max(tce, 1d);
            double yoy = prev == 0 ? -4.1 : (emission - prev) / prev * 100d;
            prev = emission;
            months.add(new AccountingView.MonthRow(MONTHS[i],
                    DashboardService.round(tce, 2), DashboardService.round(emission, 2),
                    DashboardService.round(intensity, 2), DashboardService.round(yoy, 2),
                    0d, emission > dayCarbon * 30 * 1.15 ? "偏高" : "正常"));
            totalEmission += emission;
            totalTce += tce;
        }
        for (int i = 0; i < months.size(); i++) {
            AccountingView.MonthRow row = months.get(i);
            months.set(i, new AccountingView.MonthRow(row.month(), row.activity(),
                    row.emission(), row.intensity(), row.yoy(),
                    DashboardService.round(totalEmission > 0 ? row.emission() / totalEmission : 0d, 4),
                    row.status()));
        }

        List<AccountingView.DetailRow> details = buildDetails(dayCarbon);
        AccountingView.Summary summary = new AccountingView.Summary(
                DashboardService.round(totalEmission, 2),
                DashboardService.round(totalTce, 2),
                DashboardService.round(totalEmission * 1000d / Math.max(totalTce, 1d), 2),
                -2.4,
                ANNUAL_QUOTA,
                DashboardService.round(Math.min(1d, totalEmission / ANNUAL_QUOTA) * 100d, 1),
                "GB/T 32150-2015 工业企业温室气体排放核算和报告通则");
        return new AccountingView(year, summary, months, details);
    }

    private List<AccountingView.DetailRow> buildDetails(double dayCarbon) {
        List<AccountingView.DetailRow> details = new ArrayList<>();
        for (EnergyType type : EnergyType.values()) {
            double activity = energyDataService.consumptionToday(type) * 30d;
            double emission = activity * type.getCarbonFactor() / 1000d;
            details.add(new AccountingView.DetailRow(
                    type.getLabel(), type.getCode(),
                    DashboardService.round(activity, 3), type.getUnit(),
                    type.getCarbonFactor(), "kgCO₂/" + type.getUnit(),
                    DashboardService.round(emission, 3),
                    DashboardService.round(dayCarbon > 0 ? emission / (dayCarbon * 30d) : 0d, 4),
                    String.format(Locale.CHINA, "%,.3f %s × %.4f kgCO₂/%s ÷ 1000 = %,.3f tCO₂e",
                            activity, type.getUnit(), type.getCarbonFactor(), type.getUnit(), emission)));
        }
        // 绿电抵扣（负数行）
        double greenActivity = energyDataService.consumptionToday(EnergyType.ELECTRICITY) * 30d * 0.186d;
        double offset = greenActivity * 0.5703d / 1000d;
        details.add(new AccountingView.DetailRow("绿电抵扣", "greenPower",
                DashboardService.round(greenActivity, 3), "kWh", -0.5703, "kgCO₂/kWh",
                DashboardService.round(-offset, 3), 0d,
                String.format(Locale.CHINA, "%,.3f kWh × 0.5703 kgCO₂/kWh ÷ 1000 = -%,.3f tCO₂e（绿电抵扣）",
                        greenActivity, offset)));
        return details;
    }

    /** 碳排放趋势预警：当前小时排放 vs 历史均值基准 */
    public AlertView alerts() {
        double[][] hourly = energyDataService.hourlyConsumption();
        int hours = energyDataService.elapsedHours();
        List<String> labels = new ArrayList<>();
        List<Double> emissions = new ArrayList<>();
        for (int h = 0; h < hours; h++) {
            labels.add(String.format("%02d:00", h));
            double kg = 0d;
            for (EnergyType type : EnergyType.values()) {
                kg += hourly[h][type.getColumnIndex()] * type.getCarbonFactor();
            }
            emissions.add(DashboardService.round(kg / 1000d, 3));
        }
        double mean = emissions.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        double threshold = mean * 1.12d;
        List<Double> baseline = new ArrayList<>();
        emissions.forEach(e -> baseline.add(DashboardService.round(mean, 3)));

        List<AlertView.Item> items = new ArrayList<>();
        for (int h = 0; h < emissions.size(); h++) {
            double value = emissions.get(h);
            if (value > threshold && value > 0) {
                double deviation = threshold > 0 ? (value - threshold) / threshold * 100d : 0d;
                items.add(new AlertView.Item(
                        labels.get(h),
                        deviation > 20 ? "danger" : "warning",
                        deviation > 20 ? "碳排放显著超出阈值" : "碳排放超出基准区间",
                        String.format(Locale.CHINA, "%s 时段排放 %.3f tCO₂e，历史均值 %.3f tCO₂e，阈值 %.3f tCO₂e",
                                labels.get(h), value, mean, threshold),
                        DashboardService.round(value, 3), DashboardService.round(threshold, 3),
                        DashboardService.round(deviation, 1)));
            }
        }
        items.sort((a, b) -> Double.compare(b.value(), a.value()));
        return new AlertView(DashboardService.round(mean, 3), DashboardService.round(threshold, 3),
                labels, emissions, baseline, items);
    }
}
