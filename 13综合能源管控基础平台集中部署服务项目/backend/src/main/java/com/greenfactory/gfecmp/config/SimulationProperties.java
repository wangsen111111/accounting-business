package com.greenfactory.gfecmp.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 模拟采集参数。
 * <p>
 * 两种模式：
 * <ul>
 *   <li><b>clock</b>（默认）：按真实时钟 1:1 对齐。样本表 2025-01-01 15:18:35 的数据，
 *       显示为今天 15:18:35；现实过 24 点后自动从样本表 00:00 重新开始。</li>
 *   <li><b>replay</b>：快进回放。启动时按 start-offset-minutes 定位，每个 tick 推进 step-points 个点，
 *       走到表尾就"跨天"（日期 +1）继续，适合快速演示。</li>
 * </ul>
 */
@ConfigurationProperties(prefix = "gfecmp.simulation")
public class SimulationProperties {

    /** clock = 真实时间 1:1 对齐；replay = 倍速回放 */
    private String mode = "clock";

    /** 定时刷新周期（毫秒），默认 20000 = 20 秒 */
    private long tickMillis = 20_000L;

    /** replay 模式下每个 tick 推进的采样点数量（样本表为 10 秒一个点） */
    private int stepPoints = 30;

    /** replay 模式下启动时快进到当天的第几分钟（默认 08:00） */
    private int startOffsetMinutes = 8 * 60;

    /** 数据源文件（classpath 相对路径或本地路径） */
    private String dataFile = "classpath:data/2025年1月1日_累计能耗样本.xlsx";

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public long getTickMillis() {
        return tickMillis;
    }

    public void setTickMillis(long tickMillis) {
        this.tickMillis = tickMillis;
    }

    public int getStepPoints() {
        return stepPoints;
    }

    public void setStepPoints(int stepPoints) {
        this.stepPoints = stepPoints;
    }

    public int getStartOffsetMinutes() {
        return startOffsetMinutes;
    }

    public void setStartOffsetMinutes(int startOffsetMinutes) {
        this.startOffsetMinutes = startOffsetMinutes;
    }

    public String getDataFile() {
        return dataFile;
    }

    public void setDataFile(String dataFile) {
        this.dataFile = dataFile;
    }
}
