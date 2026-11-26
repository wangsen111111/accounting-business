package com.greenfactory.gfecmp.service;

import com.greenfactory.gfecmp.dto.FootprintView;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 产品碳足迹核算（FR-005）：覆盖原材料、生产、运输、销售、使用、回收六大环节。
 */
@Service
public class FootprintService {

    public FootprintView build() {
        List<FootprintView.Stage> stages = List.of(
                new FootprintView.Stage("原材料获取", "钢坯、合金、涂料等上游材料", 7.42, 0.577, "CLCD 中国生命周期数据库", "#1F6B45"),
                new FootprintView.Stage("生产制造", "炼钢、轧制、热处理工序能耗", 3.15, 0.245, "企业实测 + 电网因子", "#2F9E63"),
                new FootprintView.Stage("运输配送", "厂内倒运 + 干线运输 380km", 0.86, 0.067, "柴油 0.86 kgCO₂/L·km", "#5FB77F"),
                new FootprintView.Stage("销售环节", "仓储、包装、渠道分摊", 0.43, 0.033, "行业平均系数", "#94CBA6"),
                new FootprintView.Stage("使用阶段", "下游客户加工使用能耗", 0.42, 0.033, "ISO 14067 使用场景假设", "#F2A93B"),
                new FootprintView.Stage("回收处理", "废钢回收再利用率 92%", 0.58, 0.045, "回收抵扣模型", "#D9A25F")
        );
        double total = stages.stream().mapToDouble(FootprintView.Stage::emission).sum();
        double greenOffset = 1.35;
        return new FootprintView("绿色低碳冷轧钢板", "1.0mm × 1250mm × C",
                DashboardService.round(total, 3), "tCO₂e/t",
                greenOffset, DashboardService.round(total - greenOffset, 3), stages);
    }
}
