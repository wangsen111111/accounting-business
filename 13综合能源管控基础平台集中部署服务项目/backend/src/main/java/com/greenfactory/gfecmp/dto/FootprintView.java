package com.greenfactory.gfecmp.dto;

import java.util.List;

/**
 * 产品碳足迹（FR-005，ISO 14067）。
 */
public record FootprintView(
        String product,
        String specification,
        double total,
        String unit,
        double greenOffset,
        double netTotal,
        List<Stage> stages
) {

    public record Stage(String name, String description, double emission, double ratio,
                        String factorSource, String color) {
    }
}
