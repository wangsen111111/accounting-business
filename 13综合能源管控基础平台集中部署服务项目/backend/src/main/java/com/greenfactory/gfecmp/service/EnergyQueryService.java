package com.greenfactory.gfecmp.service;

import com.greenfactory.gfecmp.common.PageResult;
import com.greenfactory.gfecmp.dto.AnalysisView;
import com.greenfactory.gfecmp.dto.EnergyRow;
import com.greenfactory.gfecmp.model.EnergyPoint;
import com.greenfactory.gfecmp.model.EnergyType;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

/**
 * 能耗查询 / 历史追溯 / 强度计算。
 * <p>
 * 明细数据来自样本表（真实解析结果）；月度数据为演示数据，
 * 由样本表的日总量按季节系数放大生成，用于展示 GB/T 2589 的计算口径。
 */
@Service
public class EnergyQueryService {

    private static final DateTimeFormatter TIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
    /** 日产量（吨，演示值）：用于折算单位产品综合能耗 */
    private static final double DAILY_OUTPUT_TONS = 32d;
    private static final double[] MONTH_FACTOR = {
            0.86, 0.78, 0.93, 0.97, 1.02, 1.08, 1.19, 1.22, 1.05, 0.98, 0.95, 0.90
    };
    private static final String[] MONTHS = {
            "2025-01", "2025-02", "2025-03", "2025-04", "2025-05", "2025-06",
            "2025-07", "2025-08", "2025-09", "2025-10", "2025-11", "2025-12"
    };

    private final EnergyDataService energyDataService;

    public EnergyQueryService(EnergyDataService energyDataService) {
        this.energyDataService = energyDataService;
    }

    /**
     * 分段明细：默认按 5 分钟聚合成一条记录，最新的排在最前。
     */
    public PageResult<EnergyRow> records(String energyType, int page, int size, String keyword, int bucketMinutes) {
        EnergyType type = EnergyType.of(energyType);
        int[][] buckets = energyDataService.buckets(bucketMinutes);
        List<EnergyRow> all = new ArrayList<>();
        for (int[] bucket : buckets) {
            EnergyPoint start = energyDataService.pointAt(bucket[0]);
            EnergyPoint end = energyDataService.pointAt(bucket[1]);
            double value = Math.max(0d, end.get(type) - start.get(type));
            String time = energyDataService.displayTimeOf(end).format(TIME_FMT);
            if (keyword != null && !keyword.isBlank()
                    && !time.contains(keyword) && !type.getLabel().contains(keyword)
                    && !type.getDeviceName().contains(keyword)) {
                continue;
            }
            all.add(new EnergyRow(0, time, type.getCode(), type.getLabel(), type.getDeviceName(),
                    DashboardService.round(value, 3), type.getUnit(),
                    DashboardService.round(end.get(type), 3),
                    DashboardService.round(value * type.getCarbonFactor() / 1000d, 4)));
        }
        Collections.reverse(all);
        List<EnergyRow> indexed = new ArrayList<>(all.size());
        for (int i = 0; i < all.size(); i++) {
            EnergyRow row = all.get(i);
            indexed.add(new EnergyRow(i + 1, row.time(), row.energyType(), row.energyName(),
                    row.device(), row.value(), row.unit(), row.cumulative(), row.carbon()));
        }
        return PageResult.of(indexed, page, size);
    }

    /** 历史追溯曲线：按小时或 10 分钟聚合 */
    public EnergyRow.SeriesView series(String energyType, String granularity) {
        EnergyType type = EnergyType.of(energyType);
        int interval = "minute10".equalsIgnoreCase(granularity) ? 600 : 3600;
        int total = energyDataService.getCursor() + 1;
        int step = Math.max(1, (int) Math.ceil(interval / (double) energyDataService.getIntervalSeconds()));
        List<String> labels = new ArrayList<>();
        List<Double> cumulative = new ArrayList<>();
        List<Double> consumption = new ArrayList<>();
        for (int i = 0; i < total; i += step) {
            int end = Math.min(total - 1, i + step);
            EnergyPoint point = energyDataService.pointAt(end);
            labels.add(energyDataService.pointAt(i).time().format(DateTimeFormatter.ofPattern("HH:mm")));
            cumulative.add(DashboardService.round(point.get(type), 2));
            consumption.add(DashboardService.round(energyDataService.consumptionBetween(type, i, end), 2));
        }
        return new EnergyRow.SeriesView(labels, cumulative, consumption, type.getUnit());
    }

    /** 12 个月能源消费量与强度（GB/T 2589），日总量按当前进度折算成整日口径 */
    public AnalysisView analysis() {
        double tceDay = Math.max(energyDataService.fullDayTce(), 0.1d);
        double outputDay = DAILY_OUTPUT_TONS;
        Random random = new Random(20250101L);

        List<AnalysisView.Row> rows = new ArrayList<>();
        List<String> months = new ArrayList<>();
        List<Double> tceSeries = new ArrayList<>();
        List<Double> outputSeries = new ArrayList<>();
        List<Double> intensitySeries = new ArrayList<>();

        double sumTce = 0d;
        double sumOutput = 0d;
        double prevIntensity = 0d;

        for (int i = 0; i < MONTHS.length; i++) {
            int days = 30;
            double tce = tceDay * days * MONTH_FACTOR[i] * (0.97 + random.nextDouble() * 0.06);
            double output = outputDay * days * MONTH_FACTOR[i] * (0.99 + random.nextDouble() * 0.04);
            double intensity = tce * 1000d / Math.max(output, 1d);
            double outputValueIntensity = tce * 1000d / Math.max(output * 0.55d, 1d);
            double yoy = prevIntensity == 0 ? -3.2 : (intensity - prevIntensity) / prevIntensity * 100d;
            prevIntensity = intensity;
            String level = intensity < 92 ? "优秀" : intensity < 100 ? "达标" : "需优化";

            months.add(MONTHS[i]);
            tceSeries.add(DashboardService.round(tce, 2));
            outputSeries.add(DashboardService.round(output, 1));
            intensitySeries.add(DashboardService.round(intensity, 2));
            rows.add(new AnalysisView.Row(MONTHS[i], DashboardService.round(tce, 2),
                    DashboardService.round(output, 1), DashboardService.round(intensity, 2),
                    DashboardService.round(outputValueIntensity, 2), DashboardService.round(yoy, 2), level));
            sumTce += tce;
            sumOutput += output;
        }

        double avgIntensity = sumTce * 1000d / Math.max(sumOutput, 1d);
        AnalysisView.Summary summary = new AnalysisView.Summary(
                DashboardService.round(sumTce, 2),
                DashboardService.round(sumOutput, 1),
                DashboardService.round(avgIntensity, 2),
                DashboardService.round(sumTce * 1000d / Math.max(sumOutput * 0.55d, 1d), 2),
                -2.8,
                "GB/T 2589-2020 综合能耗计算通则");
        return new AnalysisView(months, tceSeries, outputSeries, intensitySeries, rows, summary);
    }
}
