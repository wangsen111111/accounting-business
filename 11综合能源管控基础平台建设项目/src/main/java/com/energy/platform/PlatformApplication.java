package com.energy.platform;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class PlatformApplication {
    public static void main(String[] args) { SpringApplication.run(PlatformApplication.class, args); }

    @Bean
    public CommandLineRunner seedData(JdbcTemplate jdbc) {
        return args -> {
            Integer count = jdbc.queryForObject("SELECT COUNT(*) FROM energy_device", Integer.class);
            if (count != null && count == 0) {
                String[][] devices = {
                    {"P-001","1号变压器","电力","综合能源站","在线","428.60","kW"},
                    {"P-002","配电房总表","电力","A区配电房","在线","326.20","kW"},
                    {"W-001","园区总水表","水务","供水泵房","在线","86.40","m³/h"},
                    {"W-002","循环水泵","水务","冷却站","维护中","42.10","m³/h"},
                    {"G-001","天然气总表","燃气","燃气调压站","在线","125.80","m³/h"},
                    {"G-002","锅炉燃气表","燃气","锅炉房","离线","0.00","m³/h"},
                    {"P-003","空压机电表","电力","动力中心","在线","98.30","kW"},
                    {"W-003","生活区水表","水务","生活区","在线","18.60","m³/h"}
                };
                for (String[] d : devices) jdbc.update("INSERT INTO energy_device(device_code,device_name,energy_type,location,status,metric_value,unit) VALUES(?,?,?,?,?,?,?)", (Object[]) d);
                jdbc.update("INSERT INTO energy_alarm(alarm_title,energy_type,alarm_level,alarm_status,detail) VALUES(?,?,?,?,?)", "锅炉房燃气表通讯中断", "燃气", "严重", "待处理", "设备连续 5 分钟未上报数据");
                jdbc.update("INSERT INTO energy_alarm(alarm_title,energy_type,alarm_level,alarm_status,detail) VALUES(?,?,?,?,?)", "A区配电负荷接近阈值", "电力", "一般", "处理中", "当前负荷达到设定阈值的 92%");
                jdbc.update("INSERT INTO energy_alarm(alarm_title,energy_type,alarm_level,alarm_status,detail) VALUES(?,?,?,?,?)", "循环水泵运行状态异常", "水务", "提示", "已处理", "设备状态已恢复，等待复核");
                jdbc.update("INSERT INTO energy_work_order(order_no,title,device_name,assignee,priority,status) VALUES(?,?,?,?,?,?)", "WO20261119001", "锅炉房燃气表通讯检查", "锅炉燃气表", "张工", "高", "待接单");
                jdbc.update("INSERT INTO energy_work_order(order_no,title,device_name,assignee,priority,status) VALUES(?,?,?,?,?,?)", "WO20261119002", "循环水泵巡检保养", "循环水泵", "李工", "普通", "处理中");
            }
        };
    }
}
