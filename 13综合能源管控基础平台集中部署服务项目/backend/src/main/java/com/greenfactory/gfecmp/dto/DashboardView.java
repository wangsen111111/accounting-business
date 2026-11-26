package com.greenfactory.gfecmp.dto;

import java.util.List;

/**
 * 首页/驾驶舱聚合数据。
 */
public record DashboardView(
        String simulatedTime,
        String dataDate,
        int nextRefreshSeconds,
        Kpi kpi,
        List<MixItem> energyMix,
        List<MixItem> carbonMix,
        Trend energyTrend,
        Trend carbonTrend,
        List<AlertItem> alerts,
        List<DeviceState> devices
) {

    /** 顶部关键指标 */
    public record Kpi(
            double totalEnergyTce,
            double totalCarbon,
            double carbonIntensity,
            double electricityToday,
            double waterToday,
            double naturalGasToday,
            double powerNow,
            double greenRatio,
            double outputToday,
            double progress,
            double carbonPerTce
    ) {
    }

    /** 能源结构 / 碳排结构中的一项 */
    public record MixItem(
            String code,
            String name,
            double value,
            String unit,
            double tce,
            double carbon,
            double ratio,
            String color
    ) {
    }

    /** 折线图：labels + 多条 series */
    public record Trend(List<String> labels, List<Series> series) {
    }

    public record Series(String code, String name, String unit, List<Double> data) {
    }

    /** 预警条目 */
    public record AlertItem(String time, String level, String title, String content,
                            double value, double threshold) {
    }

    /** 设备（仪表）状态 */
    public record DeviceState(String deviceId, String name, String energyType, String unit,
                              boolean online, double reading, double today, String lastTime) {
    }
}
