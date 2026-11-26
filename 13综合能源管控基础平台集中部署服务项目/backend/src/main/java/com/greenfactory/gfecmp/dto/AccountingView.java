package com.greenfactory.gfecmp.dto;

import java.util.List;

/**
 * 碳排放核算管理。
 */
public record AccountingView(
        int year,
        Summary summary,
        List<MonthRow> months,
        List<DetailRow> details
) {

    public record Summary(double totalEmission, double totalTce, double intensity,
                          double yoy, double quota, double quotaUsage, String standard) {
    }

    public record MonthRow(String month, double activity, double emission, double intensity,
                           double yoy, double ratio, String status) {
    }

    public record DetailRow(String source, String energyType, double activity, String activityUnit,
                            double factor, String factorUnit, double emission, double ratio, String formula) {
    }
}
