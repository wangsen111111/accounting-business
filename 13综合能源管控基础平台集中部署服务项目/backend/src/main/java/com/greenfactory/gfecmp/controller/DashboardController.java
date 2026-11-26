package com.greenfactory.gfecmp.controller;

import com.greenfactory.gfecmp.common.ApiResult;
import com.greenfactory.gfecmp.dto.DashboardView;
import com.greenfactory.gfecmp.dto.MetaView;
import com.greenfactory.gfecmp.service.DashboardService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 首页驾驶舱与元数据接口。
 */
@RestController
@RequestMapping("/api")
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    /** 驾驶舱聚合数据：KPI + 能源结构 + 趋势 + 预警 + 设备状态 */
    @GetMapping("/dashboard")
    public ApiResult<DashboardView> dashboard() {
        return ApiResult.ok(dashboardService.build());
    }

    /** 能源类型字典 + 模拟采集运行状态（前端用来显示"下次刷新"倒计时） */
    @GetMapping("/meta")
    public ApiResult<MetaView> meta() {
        return ApiResult.ok(dashboardService.meta());
    }

    @GetMapping("/health")
    public ApiResult<String> health() {
        return ApiResult.ok("UP");
    }
}
