package com.greenfactory.gfecmp.service;

import com.greenfactory.gfecmp.config.SimulationProperties;
import com.greenfactory.gfecmp.datasource.EnergySampleDataSource;
import com.greenfactory.gfecmp.model.EnergyPoint;
import com.greenfactory.gfecmp.model.EnergyType;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 模拟采集引擎：用一个游标在样本表上"实时回放"。
 * <p>
 * 每 20 秒（可配置）推进一次游标，等价于平台每 20 秒从数据表轮询一次最新数据，
 * 所有对外接口都基于当前游标位置计算，因此前端刷新即可看到数据变化。
 */
@Service
public class EnergyDataService {

    private static final Logger log = LoggerFactory.getLogger(EnergyDataService.class);

    private final EnergySampleDataSource dataSource;
    private final SimulationProperties properties;
    private final List<EnergyPoint> points;

    /** 当前回放到的下标 */
    private volatile int cursor;
    /** 已完整回放的天数（跨天次数） */
    private volatile int loop;
    private volatile long lastTickAt = System.currentTimeMillis();

    /** 样本表整日合计量，启动时算一次（每个月度/年度指标都以它为基准） */
    private final double fullDayTce;
    private final double fullDayCarbon;

    public EnergyDataService(EnergySampleDataSource dataSource, SimulationProperties properties) {
        this.dataSource = dataSource;
        this.properties = properties;
        this.points = dataSource.getPoints();

        EnergyPoint first = points.get(0);
        EnergyPoint last = points.get(points.size() - 1);
        double kgce = 0d;
        double kgco2 = 0d;
        for (EnergyType type : EnergyType.values()) {
            double amount = Math.max(0d, last.get(type) - first.get(type));
            kgce += amount * type.getCoalFactor();
            kgco2 += amount * type.getCarbonFactor();
        }
        this.fullDayTce = kgce / 1000d;
        this.fullDayCarbon = kgco2 / 1000d;
    }

    @PostConstruct
    public void init() {
        if (isClockMode()) {
            this.cursor = indexOfTime(LocalTime.now());
            log.info("[模拟引擎] 时钟对齐模式（1:1）：当前时间 {}，使用样本表 {} 的同刻度数据，刷新周期 {} ms",
                    LocalDateTime.now().withNano(0), points.get(cursor).time().toLocalTime(),
                    properties.getTickMillis());
            return;
        }
        int offsetPoints = (int) (properties.getStartOffsetMinutes() * 60.0 / dataSource.getIntervalSeconds());
        this.cursor = Math.min(Math.max(0, offsetPoints), points.size() - 1);
        log.info("[模拟引擎] 倍速回放模式：起始位置 {} / {}，对应时间 {}，每个 tick 推进 {} 个点，周期 {} ms",
                cursor, points.size(), currentTime(), properties.getStepPoints(), properties.getTickMillis());
    }

    /**
     * 定时刷新（默认 20 秒一次）。
     * <ul>
     *   <li>clock 模式：游标 = 真实时钟在样本表里的对应位置，1:1 对齐；过 24 点后自然回到 00:00。</li>
     *   <li>replay 模式：按 step-points 快进；走到表尾进入"次日"，日期 +1 天并回到 00:00。</li>
     * </ul>
     */
    @Scheduled(fixedRateString = "${gfecmp.simulation.tick-millis:20000}")
    public void tick() {
        if (isClockMode()) {
            int idx = indexOfTime(LocalTime.now());
            if (idx < cursor) {
                loop++;
                log.info("[模拟引擎] 已跨过零点，样本表从 00:00 重新开始（已跨 {} 天）", loop);
            }
            cursor = idx;
            lastTickAt = System.currentTimeMillis();
            return;
        }
        int step = Math.max(1, properties.getStepPoints());
        int next = cursor + step;
        if (next >= points.size()) {
            // 样本表只有一天，回卷表示"进入新的一天"：游标归零 + 日期滚动
            cursor = 0;
            loop++;
            log.info("[模拟引擎] 样本表回放完成一轮，进入 {} 的模拟日（第 {} 轮）",
                    currentTime().toLocalDate(), loop);
        } else {
            cursor = next;
        }
        lastTickAt = System.currentTimeMillis();
    }

    /** 是否为真实时钟 1:1 对齐模式 */
    public boolean isClockMode() {
        return !"replay".equalsIgnoreCase(properties.getMode());
    }

    public String getMode() {
        return isClockMode() ? "clock" : "replay";
    }

    // ================= 基础读取 =================

    public int getCursor() {
        return cursor;
    }

    public int getTotalPoints() {
        return points.size();
    }

    public int getLoop() {
        return loop;
    }

    public EnergyPoint currentPoint() {
        return points.get(cursor);
    }

    /** 当前模拟时刻：时钟模式下就是真实时间，回放模式下由游标决定 */
    public LocalDateTime currentTime() {
        if (isClockMode()) {
            return LocalDateTime.now().withNano(0);
        }
        return displayTimeOf(points.get(cursor));
    }

    /**
     * 把样本表里的原始时刻映射到"当前日期"。
     * <ul>
     *   <li>clock 模式：样本表 15:18:30 的点 → 今天 15:18:30（1:1 对齐真实时钟）</li>
     *   <li>replay 模式：每回卷一次 loop+1，日期随之 +1 天（2025-01-02、2025-01-03 …）</li>
     * </ul>
     */
    public LocalDateTime displayTimeOf(EnergyPoint point) {
        if (isClockMode()) {
            return LocalDateTime.of(LocalDate.now(), point.time().toLocalTime());
        }
        return point.time().plusDays(loop);
    }

    /** 当前模拟日期 */
    public LocalDate currentDate() {
        return currentTime().toLocalDate();
    }

    /** 当前处于第几天（从 1 开始） */
    public int getDayNo() {
        return loop + 1;
    }

    public long getLastTickAt() {
        return lastTickAt;
    }

    public long getTickMillis() {
        return properties.getTickMillis();
    }

    public int getStepPoints() {
        return properties.getStepPoints();
    }

    public int getIntervalSeconds() {
        return dataSource.getIntervalSeconds();
    }

    public String getSourceName() {
        return dataSource.getSourceName();
    }

    /** 当天 0 点至今某介质的累计消耗量 */
    public double consumptionToday(EnergyType type) {
        return Math.max(0d, currentPoint().get(type) - points.get(0).get(type));
    }

    public Map<EnergyType, Double> consumptionToday() {
        Map<EnergyType, Double> map = new LinkedHashMap<>();
        for (EnergyType type : EnergyType.values()) {
            map.put(type, consumptionToday(type));
        }
        return map;
    }

    /** 当前瞬时功率/流量（按最后一个采样间隔折算到每小时） */
    public double instantaneous(EnergyType type) {
        if (cursor == 0) {
            return 0d;
        }
        EnergyPoint prev = points.get(cursor - 1);
        EnergyPoint cur = points.get(cursor);
        double delta = cur.get(type) - prev.get(type);
        return Math.max(0d, delta) * 3600d / Math.max(1, getIntervalSeconds());
    }

    /** 今日折标煤合计（tce） */
    public double totalTceToday() {
        double kgce = 0d;
        for (EnergyType type : EnergyType.values()) {
            kgce += consumptionToday(type) * type.getCoalFactor();
        }
        return kgce / 1000d;
    }

    /** 今日碳排放合计（tCO2） */
    public double totalCarbonToday() {
        double kg = 0d;
        for (EnergyType type : EnergyType.values()) {
            kg += consumptionToday(type) * type.getCarbonFactor();
        }
        return kg / 1000d;
    }

    /**
     * 整日综合能耗（tce）。
     * 样本表每天回放的是同一份数据，所以"整日"是一个常量，
     * 月度/年度指标用它做基准，跨天时不会因为进度归零而抖动。
     */
    public double fullDayTce() {
        return fullDayTce;
    }

    /** 整日碳排放（tCO2），口径同上 */
    public double fullDayCarbon() {
        return fullDayCarbon;
    }

    /**
     * 分时（小时）消耗量，数组下标 0~23。
     * 只统计到当前模拟时刻，未到来的小时为 0。
     */
    public double[][] hourlyConsumption() {
        double[][] hours = new double[24][EnergyType.values().length];
        int idx = 0;
        while (idx < cursor) {
            EnergyPoint cur = points.get(idx);
            EnergyPoint next = points.get(idx + 1);
            int hour = cur.time().getHour();
            for (EnergyType type : EnergyType.values()) {
                hours[hour][type.getColumnIndex()] += Math.max(0d, next.get(type) - cur.get(type));
            }
            idx++;
        }
        return hours;
    }

    /** 已完成的小时数（含当前小时） */
    public int elapsedHours() {
        return Math.min(24, currentTime().getHour() + 1);
    }

    /** 当天进度 0~1 */
    public double dayProgress() {
        LocalTime time = currentTime().toLocalTime();
        return (time.toSecondOfDay()) / 86400d;
    }

    public Duration dayElapsed() {
        return Duration.ofSeconds(currentTime().toLocalTime().toSecondOfDay());
    }

    /** 指定小时区间内的消耗量 */
    public double consumptionBetween(EnergyType type, int fromIndex, int toIndex) {
        int from = Math.max(0, Math.min(fromIndex, points.size() - 1));
        int to = Math.max(from, Math.min(toIndex, points.size() - 1));
        return Math.max(0d, points.get(to).get(type) - points.get(from).get(type));
    }

    public EnergyPoint pointAt(int index) {
        return points.get(Math.max(0, Math.min(index, points.size() - 1)));
    }

    /** 采样点下标 -> 指定时间（当天内），用于按时间筛选 */
    public int indexOfTime(LocalTime time) {
        int index = (int) (time.toSecondOfDay() / (double) getIntervalSeconds());
        return Math.max(0, Math.min(index, points.size() - 1));
    }

    /**
     * 按固定分钟数分桶，返回每个桶的（起始下标，结束下标）。
     * 用于"能耗数据查询"的分段明细；不足一个完整桶的尾部暂不输出。
     */
    public int[][] buckets(int bucketMinutes) {
        int perBucket = Math.max(1, (int) Math.ceil(bucketMinutes * 60d / getIntervalSeconds()));
        List<int[]> result = new java.util.ArrayList<>();
        for (int start = 0; start + 1 <= cursor; start += perBucket) {
            int end = Math.min(cursor, start + perBucket);
            if (end > start) {
                result.add(new int[]{start, end});
            }
        }
        return result.toArray(new int[0][]);
    }
}
