package com.greenfactory.gfecmp.dto;

import java.util.List;

/**
 * 元数据：能源类型字典 + 模拟采集运行状态。
 */
public record MetaView(
        List<EnergyTypeItem> energyTypes,
        Refresh refresh,
        String dataSource,
        String version
) {

    public record EnergyTypeItem(String code, String name, String unit, String device,
                                 double carbonFactor, double coalFactor, String color) {
    }

    public record Refresh(long tickMillis, int stepPoints, int cursor, int totalPoints,
                          String simulatedTime, int intervalSeconds, int loop, String lastTickAt,
                          String mode) {
    }
}
