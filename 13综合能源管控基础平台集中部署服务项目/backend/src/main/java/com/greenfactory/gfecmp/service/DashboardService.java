package com.greenfactory.gfecmp.service;

import com.greenfactory.gfecmp.dto.DashboardView;
import com.greenfactory.gfecmp.dto.MetaView;
import com.greenfactory.gfecmp.model.EnergyType;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

/**
 * 首页驾驶舱数据组装。
 */
@Service
public class DashboardService {

    private static final DateTimeFormatter TIME_FMT = new DateTimeFormatterBuilder()
            .appendPattern("HH:mm").toFormatter(Locale.CHINA);
    private static final DateTimeFormatter DATETIME_FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    /** 日产量（吨，演示值），用于折算单位产品碳排放强度 */
    private static final double DAILY_OUTPUT_TONS = 32d;
    /** 绿电交易占比（演示值） */
    private static final double GREEN_POWER_RATIO = 18.6d;

    private final EnergyDataService energyDataService;

    public DashboardService(EnergyDataService energyDataService) {
        this.energyDataService = energyDataService;
    }

    public DashboardView build() {
        double[][] hourly = energyDataService.hourlyConsumption();
        int hours = energyDataService.elapsedHours();
        double progress = energyDataService.dayProgress();
        double outputToday = DAILY_OUTPUT_TONS * progress;

        double totalCarbon = energyDataService.totalCarbonToday();
        double totalTce = energyDataService.totalTceToday();

        DashboardView.Kpi kpi = new DashboardView.Kpi(
                round(totalTce, 3),
                round(totalCarbon, 3),
                round(outputToday > 0 ? totalCarbon / outputToday : 0, 4),
                round(energyDataService.consumptionToday(EnergyType.ELECTRICITY), 2),
                round(energyDataService.consumptionToday(EnergyType.WATER), 2),
                round(energyDataService.consumptionToday(EnergyType.NATURAL_GAS), 2),
                round(energyDataService.instantaneous(EnergyType.ELECTRICITY), 2),
                GREEN_POWER_RATIO,
                round(outputToday, 1),
                round(progress * 100, 1),
                round(totalTce > 0 ? totalCarbon / totalTce : 0, 3)
        );

        return new DashboardView(
                energyDataService.currentTime().format(DATETIME_FMT),
                energyDataService.currentTime().toLocalDate().toString(),
                (int) (energyDataService.getTickMillis() / 1000),
                kpi,
                buildEnergyMix(hourly),
                buildCarbonMix(),
                buildEnergyTrend(hourly, hours),
                buildCarbonTrend(hourly, hours),
                buildAlerts(hourly, hours),
                buildDevices()
        );
    }

    private List<DashboardView.MixItem> buildEnergyMix(double[][] hourly) {
        List<DashboardView.MixItem> items = new ArrayList<>();
        double totalTce = 0d;
        for (EnergyType type : EnergyType.values()) {
            totalTce += energyDataService.consumptionToday(type) * type.getCoalFactor() / 1000d;
        }
        for (EnergyType type : EnergyType.values()) {
            double value = energyDataService.consumptionToday(type);
            double tce = value * type.getCoalFactor() / 1000d;
            items.add(new DashboardView.MixItem(
                    type.getCode(), type.getLabel(), round(value, 3), type.getUnit(),
                    round(tce, 4), round(value * type.getCarbonFactor() / 1000d, 4),
                    round(totalTce > 0 ? tce / totalTce : 0, 4), type.getColor()));
        }
        items.sort(Comparator.comparingDouble(DashboardView.MixItem::tce).reversed());
        return items;
    }

    private List<DashboardView.MixItem> buildCarbonMix() {
        List<DashboardView.MixItem> items = new ArrayList<>();
        double total = energyDataService.totalCarbonToday();
        for (EnergyType type : EnergyType.values()) {
            double value = energyDataService.consumptionToday(type);
            double carbon = value * type.getCarbonFactor() / 1000d;
            items.add(new DashboardView.MixItem(
                    type.getCode(), type.getLabel(), round(carbon, 4), "tCO₂e",
                    round(value * type.getCoalFactor() / 1000d, 4), round(carbon, 4),
                    round(total > 0 ? carbon / total : 0, 4), type.getColor()));
        }
        items.sort(Comparator.comparingDouble(DashboardView.MixItem::carbon).reversed());
        return items;
    }

    private DashboardView.Trend buildEnergyTrend(double[][] hourly, int hours) {
        List<String> labels = labels(hours);
        List<DashboardView.Series> series = new ArrayList<>();
        EnergyType[] shown = {EnergyType.ELECTRICITY, EnergyType.NATURAL_GAS, EnergyType.WATER};
        for (EnergyType type : shown) {
            List<Double> data = new ArrayList<>();
            for (int h = 0; h < hours; h++) {
                data.add(round(hourly[h][type.getColumnIndex()], 2));
            }
            series.add(new DashboardView.Series(type.getCode(), type.getLabel(), type.getUnit(), data));
        }
        return new DashboardView.Trend(labels, series);
    }

    private DashboardView.Trend buildCarbonTrend(double[][] hourly, int hours) {
        List<Double> emissions = hourlyCarbon(hourly, hours);
        double mean = emissions.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        List<Double> baseline = new ArrayList<>();
        emissions.forEach(e -> baseline.add(round(mean, 3)));
        List<DashboardView.Series> series = List.of(
                new DashboardView.Series("emission", "小时碳排放", "tCO₂e", emissions),
                new DashboardView.Series("baseline", "历史均值基准", "tCO₂e", baseline));
        return new DashboardView.Trend(labels(hours), series);
    }

    private List<Double> hourlyCarbon(double[][] hourly, int hours) {
        List<Double> emissions = new ArrayList<>();
        for (int h = 0; h < hours; h++) {
            double kg = 0d;
            for (EnergyType type : EnergyType.values()) {
                kg += hourly[h][type.getColumnIndex()] * type.getCarbonFactor();
            }
            emissions.add(round(kg / 1000d, 3));
        }
        return emissions;
    }

    private List<DashboardView.AlertItem> buildAlerts(double[][] hourly, int hours) {
        List<Double> emissions = hourlyCarbon(hourly, hours);
        if (emissions.isEmpty()) {
            return List.of();
        }
        double mean = emissions.stream().mapToDouble(Double::doubleValue).average().orElse(0d);
        double threshold = mean * 1.12d;
        List<DashboardView.AlertItem> alerts = new ArrayList<>();
        for (int h = 0; h < emissions.size(); h++) {
            double value = emissions.get(h);
            if (value > threshold && value > 0) {
                double deviation = (value - threshold) / threshold * 100d;
                alerts.add(new DashboardView.AlertItem(
                        String.format("%02d:00", h),
                        deviation > 20 ? "danger" : "warning",
                        deviation > 20 ? "碳排放显著超阈值" : "碳排放超出基准",
                        String.format("%02d:00-%02d:00 时段碳排放 %.3f tCO₂e，高于历史均值 %.3f tCO₂e",
                                h, h + 1, value, mean),
                        round(value, 3), round(threshold, 3)));
            }
        }
        alerts.sort(Comparator.comparingDouble(DashboardView.AlertItem::value).reversed());
        return alerts.stream().limit(4).toList();
    }

    private List<DashboardView.DeviceState> buildDevices() {
        List<DashboardView.DeviceState> devices = new ArrayList<>();
        int i = 1;
        for (EnergyType type : EnergyType.values()) {
            devices.add(new DashboardView.DeviceState(
                    String.format("GF-%s-%02d", type.name().substring(0, 3), i++),
                    type.getDeviceName(),
                    type.getCode(),
                    type.getUnit(),
                    true,
                    round(energyDataService.currentPoint().get(type), 3),
                    round(energyDataService.consumptionToday(type), 3),
                    energyDataService.currentTime().format(TIME_FMT)));
        }
        return devices;
    }

    private List<String> labels(int hours) {
        List<String> labels = new ArrayList<>();
        for (int h = 0; h < hours; h++) {
            labels.add(String.format("%02d:00", h));
        }
        return labels;
    }

    public MetaView meta() {
        List<MetaView.EnergyTypeItem> types = new ArrayList<>();
        for (EnergyType type : EnergyType.values()) {
            types.add(new MetaView.EnergyTypeItem(type.getCode(), type.getLabel(), type.getUnit(),
                    type.getDeviceName(), type.getCarbonFactor(), type.getCoalFactor(), type.getColor()));
        }
        MetaView.Refresh refresh = new MetaView.Refresh(
                energyDataService.getTickMillis(),
                energyDataService.getStepPoints(),
                energyDataService.getCursor(),
                energyDataService.getTotalPoints(),
                energyDataService.currentTime().format(DATETIME_FMT),
                energyDataService.getIntervalSeconds(),
                energyDataService.getLoop(),
                LocalDateTime.now().format(DATETIME_FMT),
                energyDataService.getMode());
        return new MetaView(types, refresh, energyDataService.getSourceName(), "1.0.0-tech-preview");
    }

    static double round(double value, int digits) {
        double factor = Math.pow(10, digits);
        return Math.round(value * factor) / factor;
    }
}
