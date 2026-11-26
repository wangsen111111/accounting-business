package com.greenfactory.gfecmp.dto;

/**
 * 碳因子库条目。
 */
public record FactorView(
        String code,
        String name,
        String category,
        String paramType,
        double value,
        String unit,
        String source,
        String updatedAt,
        int version
) {
}
