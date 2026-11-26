package com.greenfactory.gfecmp.controller;

import com.greenfactory.gfecmp.common.ApiResult;
import com.greenfactory.gfecmp.common.PageResult;
import com.greenfactory.gfecmp.dto.AnalysisView;
import com.greenfactory.gfecmp.dto.EnergyRow;
import com.greenfactory.gfecmp.service.EnergyQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 能耗查询 / 历史追溯 / 强度计算接口。
 */
@RestController
@RequestMapping("/api/energy")
public class EnergyController {

    private final EnergyQueryService energyQueryService;

    public EnergyController(EnergyQueryService energyQueryService) {
        this.energyQueryService = energyQueryService;
    }

    /**
     * 能耗明细（按 5 分钟聚合的分段记录）。
     *
     * @param type          能源类型，默认 electricity
     * @param page          页码，从 1 开始
     * @param size          每页条数
     * @param keyword       时间/介质/设备关键字
     * @param bucketMinutes 聚合粒度（分钟）
     */
    @GetMapping("/records")
    public ApiResult<PageResult<EnergyRow>> records(
            @RequestParam(defaultValue = "electricity") String type,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "5") int bucketMinutes) {
        return ApiResult.ok(energyQueryService.records(type, page, size, keyword, bucketMinutes));
    }

    /** 历史追溯曲线：granularity = hour | minute10 */
    @GetMapping("/series")
    public ApiResult<EnergyRow.SeriesView> series(
            @RequestParam(defaultValue = "electricity") String type,
            @RequestParam(defaultValue = "hour") String granularity) {
        return ApiResult.ok(energyQueryService.series(type, granularity));
    }

    /** 能源消费量与强度计算（GB/T 2589） */
    @GetMapping("/analysis")
    public ApiResult<AnalysisView> analysis() {
        return ApiResult.ok(energyQueryService.analysis());
    }
}
