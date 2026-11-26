package com.greenfactory.gfecmp.model;

import java.time.LocalDateTime;

/**
 * 一个采样时刻的累计表计读数。
 * 样本表每 10 秒一行，数值为当天 0 点起的累计量。
 */
public record EnergyPoint(int seq, LocalDateTime time, double[] cumulative) {

    public double get(EnergyType type) {
        return cumulative[type.getColumnIndex()];
    }

    /** 与后一个点之间的增量（消耗量） */
    public double delta(EnergyType type, EnergyPoint next) {
        return Math.max(0d, next.get(type) - get(type));
    }
}
