package com.greenfactory.gfecmp.dto;

import java.util.List;

/**
 * 碳排放趋势预警。
 */
public record AlertView(
        double mean,
        double threshold,
        List<String> labels,
        List<Double> emissions,
        List<Double> baseline,
        List<Item> items
) {

    public record Item(String time, String level, String title, String content,
                       double value, double threshold, double deviation) {
    }
}
