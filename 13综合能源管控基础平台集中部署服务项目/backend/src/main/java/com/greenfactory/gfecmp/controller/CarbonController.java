package com.greenfactory.gfecmp.controller;

import com.greenfactory.gfecmp.common.ApiResult;
import com.greenfactory.gfecmp.dto.AccountingView;
import com.greenfactory.gfecmp.dto.AlertView;
import com.greenfactory.gfecmp.dto.AssetView;
import com.greenfactory.gfecmp.dto.FactorView;
import com.greenfactory.gfecmp.dto.FootprintView;
import com.greenfactory.gfecmp.service.AssetService;
import com.greenfactory.gfecmp.service.CarbonService;
import com.greenfactory.gfecmp.service.FootprintService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 碳排放管理 / 高级碳管理接口。
 */
@RestController
@RequestMapping("/api/carbon")
public class CarbonController {

    private final CarbonService carbonService;
    private final AssetService assetService;
    private final FootprintService footprintService;

    public CarbonController(CarbonService carbonService, AssetService assetService,
                            FootprintService footprintService) {
        this.carbonService = carbonService;
        this.assetService = assetService;
        this.footprintService = footprintService;
    }

    /** 碳因子库 */
    @GetMapping("/factors")
    public ApiResult<List<FactorView>> factors() {
        return ApiResult.ok(carbonService.factors());
    }

    /** 年度碳排放核算（月度汇总 + 排放源明细） */
    @GetMapping("/accounting")
    public ApiResult<AccountingView> accounting(@RequestParam(defaultValue = "2025") int year) {
        return ApiResult.ok(carbonService.accounting(year));
    }

    /** 碳排放趋势预警 */
    @GetMapping("/alerts")
    public ApiResult<AlertView> alerts() {
        return ApiResult.ok(carbonService.alerts());
    }

    /** 碳资产履约预测仪表盘 */
    @GetMapping("/asset")
    public ApiResult<AssetView> asset() {
        return ApiResult.ok(assetService.build());
    }

    /** 产品碳足迹 */
    @GetMapping("/footprint")
    public ApiResult<FootprintView> footprint() {
        return ApiResult.ok(footprintService.build());
    }
}
