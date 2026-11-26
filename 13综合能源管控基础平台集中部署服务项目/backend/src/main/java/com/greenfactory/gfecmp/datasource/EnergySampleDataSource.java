package com.greenfactory.gfecmp.datasource;

import com.greenfactory.gfecmp.config.SimulationProperties;
import com.greenfactory.gfecmp.model.EnergyPoint;
import com.greenfactory.gfecmp.model.EnergyType;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DateUtil;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * 能耗样本数据源：解析《2025年1月1日_累计能耗样本.xlsx》。
 * <p>
 * 表格结构（第 1 行为表头）：
 * <pre>
 * A timestamp | B electricity_kwh_cumulative | C water_m3_cumulative
 *             | D gasoline_l_cumulative      | E diesel_l_cumulative
 *             | F ev_electricity_kwh_cumulative | G natural_gas_m3_cumulative
 * </pre>
 * 共 8640 行，10 秒一条，覆盖 2025-01-01 全天，数值为当天 0 点起的累计读数。
 */
@Component
public class EnergySampleDataSource {

    private static final Logger log = LoggerFactory.getLogger(EnergySampleDataSource.class);

    /** 表头关键字 -> 能源类型，顺序敏感（ev_electricity 必须排在 electricity 之前） */
    private static final String[][] HEADER_RULES = {
            {"ev_electricity", EnergyType.EV_ELECTRICITY.name()},
            {"electricity", EnergyType.ELECTRICITY.name()},
            {"water", EnergyType.WATER.name()},
            {"gasoline", EnergyType.GASOLINE.name()},
            {"diesel", EnergyType.DIESEL.name()},
            {"natural_gas", EnergyType.NATURAL_GAS.name()},
    };

    private final List<EnergyPoint> points;
    private final String sourceName;
    private final int intervalSeconds;
    private final LocalDateTime startTime;

    public EnergySampleDataSource(ResourceLoader resourceLoader, SimulationProperties properties) {
        this.sourceName = properties.getDataFile();
        List<EnergyPoint> loaded = load(resourceLoader.getResource(properties.getDataFile()));
        this.points = loaded;
        this.startTime = loaded.isEmpty() ? LocalDateTime.now() : loaded.get(0).time();
        this.intervalSeconds = detectInterval(loaded);
        log.info("[数据源] {} 解析完成：{} 个采样点，{} ~ {}，采样间隔 {}s",
                sourceName, points.size(), startTime, points.get(points.size() - 1).time(), intervalSeconds);
    }

    private List<EnergyPoint> load(Resource resource) {
        List<EnergyPoint> result = new ArrayList<>();
        try (InputStream in = resource.getInputStream(); Workbook workbook = new XSSFWorkbook(in)) {
            Sheet sheet = workbook.getSheetAt(0);
            Row header = sheet.getRow(0);
            int[] columnMap = resolveColumns(header);

            for (int r = 1; r <= sheet.getLastRowNum(); r++) {
                Row row = sheet.getRow(r);
                if (row == null) {
                    continue;
                }
                Cell timeCell = row.getCell(0);
                if (timeCell == null) {
                    continue;
                }
                LocalDateTime time = readTime(timeCell);
                if (time == null) {
                    continue;
                }
                double[] values = new double[EnergyType.values().length];
                for (int i = 0; i < EnergyType.values().length; i++) {
                    Cell cell = row.getCell(columnMap[i]);
                    values[i] = cell == null ? 0d : Math.max(0d, cell.getNumericCellValue());
                }
                result.add(new EnergyPoint(result.size(), time, values));
            }
        } catch (Exception e) {
            throw new IllegalStateException("读取能耗样本表失败：" + sourceName, e);
        }
        result.sort(Comparator.comparing(EnergyPoint::time));
        // 少数表计可能因采集抖动出现回退，这里做一次单调修正，保证累计量只增不减
        for (int i = 1; i < result.size(); i++) {
            double[] prev = result.get(i - 1).cumulative();
            double[] cur = result.get(i).cumulative();
            for (int k = 0; k < cur.length; k++) {
                if (cur[k] < prev[k]) {
                    cur[k] = prev[k];
                }
            }
        }
        return List.copyOf(result);
    }

    /** 按表头关键字定位列，找不到时退化为固定 B~G 列 */
    private int[] resolveColumns(Row header) {
        int[] map = new int[EnergyType.values().length];
        for (int i = 0; i < map.length; i++) {
            map[i] = i + 1;
        }
        if (header == null) {
            return map;
        }
        for (int c = 1; c < header.getLastCellNum(); c++) {
            Cell cell = header.getCell(c);
            if (cell == null) {
                continue;
            }
            String text = cell.getStringCellValue().trim().toLowerCase();
            for (String[] rule : HEADER_RULES) {
                if (text.contains(rule[0])) {
                    EnergyType type = EnergyType.valueOf(rule[1]);
                    map[type.getColumnIndex()] = c;
                    break;
                }
            }
        }
        return map;
    }

    private LocalDateTime readTime(Cell cell) {
        if (cell.getCellType() == CellType.NUMERIC && DateUtil.isCellDateFormatted(cell)) {
            return DateUtil.getLocalDateTime(cell.getNumericCellValue());
        }
        if (cell.getCellType() == CellType.NUMERIC) {
            // 未套用日期格式的 Excel 序列号
            return DateUtil.getLocalDateTime(cell.getNumericCellValue());
        }
        String text = cell.toString().trim().replace('/', '-');
        try {
            return LocalDateTime.parse(text.replace(' ', 'T'));
        } catch (Exception ignore) {
            return null;
        }
    }

    private int detectInterval(List<EnergyPoint> data) {
        if (data.size() < 2) {
            return 10;
        }
        return (int) Math.max(1,
                java.time.Duration.between(data.get(0).time(), data.get(1).time()).getSeconds());
    }

    public List<EnergyPoint> getPoints() {
        return points;
    }

    public String getSourceName() {
        return sourceName;
    }

    public int getIntervalSeconds() {
        return intervalSeconds;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }
}
