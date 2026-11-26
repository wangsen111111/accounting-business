package com.greenfactory.gfecmp.model;

/**
 * 能源介质定义（与样本表 B~G 列一一对应）。
 * <p>
 * 排放因子取全国电网平均排放因子与《省级温室气体清单编制指南》常用值；
 * 折标煤系数取自 GB/T 2589《综合能耗计算通则》。
 */
public enum EnergyType {

    ELECTRICITY("electricity", "电", "kWh", "智能电表", 0, 0.5703, 0.1229, "#2F9E63"),
    WATER("water", "水", "m³", "智能水表", 1, 0.3440, 0.2429, "#3FA9C9"),
    GASOLINE("gasoline", "汽油", "L", "OBD 设备", 2, 2.9250, 1.0668, "#F2A93B"),
    DIESEL("diesel", "柴油", "L", "OBD 设备", 3, 3.0960, 1.2240, "#D97742"),
    EV_ELECTRICITY("evElectricity", "新能源车用电", "kWh", "充电桩计量", 4, 0.5703, 0.1229, "#6FBF8B"),
    NATURAL_GAS("naturalGas", "天然气", "m³", "气体流量计", 5, 2.1620, 1.3300, "#8C7BD8");

    private final String code;
    private final String label;
    private final String unit;
    private final String deviceName;
    /** 在样本表中的列顺序（0 起，对应 B~G 列） */
    private final int columnIndex;
    /** 碳排放因子 kgCO2 / 单位 */
    private final double carbonFactor;
    /** 折标准煤系数 kgce / 单位 */
    private final double coalFactor;
    private final String color;

    EnergyType(String code, String label, String unit, String deviceName,
               int columnIndex, double carbonFactor, double coalFactor, String color) {
        this.code = code;
        this.label = label;
        this.unit = unit;
        this.deviceName = deviceName;
        this.columnIndex = columnIndex;
        this.carbonFactor = carbonFactor;
        this.coalFactor = coalFactor;
        this.color = color;
    }

    public static EnergyType of(String code) {
        if (code == null || code.isBlank()) {
            return ELECTRICITY;
        }
        for (EnergyType type : values()) {
            if (type.code.equalsIgnoreCase(code) || type.name().equalsIgnoreCase(code)) {
                return type;
            }
        }
        return ELECTRICITY;
    }

    public String getCode() {
        return code;
    }

    public String getLabel() {
        return label;
    }

    public String getUnit() {
        return unit;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public int getColumnIndex() {
        return columnIndex;
    }

    public double getCarbonFactor() {
        return carbonFactor;
    }

    public double getCoalFactor() {
        return coalFactor;
    }

    public String getColor() {
        return color;
    }
}
