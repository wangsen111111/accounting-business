package com.greenfactory.gfecmp.dto;

import java.util.List;

/**
 * 能源消费量与强度分析（GB/T 2589）。
 */
public record AnalysisView(
        List<String> months,
        List<Double> tceSeries,
        List<Double> outputSeries,
        List<Double> intensitySeries,
        List<Row> table,
        Summary summary
) {

    public record Row(String month, double tce, double output, double intensity,
                      double outputValueIntensity, double yoy, String level) {
    }

    public record Summary(double tce, double output, double intensity,
                          double outputValueIntensity, double yoy, String standard) {
    }
}
