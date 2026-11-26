package com.greenfactory.gfecmp;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 绿色工厂能碳管理平台 - 后端启动类
 * <p>
 * 技术验证版：不接入 ESP32 / 真实仪表，全部使用
 * 《2025年1月1日_累计能耗样本.xlsx》作为唯一数据源，
 * 由定时任务每 20 秒推进一次模拟时钟，模拟"实时采集"效果。
 */
@SpringBootApplication
@EnableScheduling
@ConfigurationPropertiesScan
public class GfecmpApplication {

    public static void main(String[] args) {
        SpringApplication.run(GfecmpApplication.class, args);
    }
}
