package com.greenfactory.gfecmp.dto;

import java.util.List;

/**
 * 碳资产管理仪表盘（FR-006）。
 */
public record AssetView(
        Kpi kpi,
        List<AssetItem> assets,
        Market market,
        List<RingItem> ring,
        List<Plan> plans
) {

    public record Kpi(double quota, double ccer, double totalAsset, double emitted,
                      double balance, double marketPrice, double marketValue,
                      double progress, String riskLevel, String riskText) {
    }

    public record AssetItem(String type, String name, double quantity, String unit,
                            String period, double price, String status) {
    }

    public record Market(List<String> labels, List<Double> prices, double latest,
                         double change, double highest, double lowest) {
    }

    public record RingItem(String name, double value, String color) {
    }

    public record Plan(String name, String owner, String deadline, double saving, String status) {
    }
}
