package com.greenfactory.gfecmp.dto;

import java.util.List;

/**
 * 能耗数据查询的一行明细。
 */
public record EnergyRow(
        int seq,
        String time,
        String energyType,
        String energyName,
        String device,
        double value,
        String unit,
        double cumulative,
        double carbon
) {
    public record SeriesView(List<String> labels, List<Double> cumulative, List<Double> consumption, String unit) {
    }
}
