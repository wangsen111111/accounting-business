package com.demo.heat.service;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import java.util.*;

@Service
public class HeatService {
    private final JdbcTemplate jdbc;
    public HeatService(JdbcTemplate jdbc) { this.jdbc = jdbc; }

    public Map<String,Object> dashboard() {
        Map<String,Object> m = new LinkedHashMap<String,Object>();
        m.put("pipeCount", count("select count(*) from heat_pipe"));
        m.put("stationCount", count("select count(*) from heat_station"));
        m.put("onlineCount", count("select count(*) from heat_station where status='运行中'"));
        m.put("pendingAlarms", count("select count(*) from heat_alarm where status='待处理'"));
        m.put("totalLength", jdbc.queryForObject("select coalesce(sum(length_km),0) from heat_pipe", Double.class));
        m.put("onlineRate", m.get("stationCount").equals(0) ? 0 : Math.round(((Number)m.get("onlineCount")).doubleValue()*100/((Number)m.get("stationCount")).doubleValue()));
        return m;
    }
    private int count(String sql) { return jdbc.queryForObject(sql, Integer.class); }
    public List<Map<String,Object>> pipes() { return jdbc.queryForList("select * from heat_pipe order by id desc"); }
    public List<Map<String,Object>> stations() { return jdbc.queryForList("select * from heat_station order by id"); }
    public List<Map<String,Object>> latestAlarms() { return jdbc.queryForList("select * from heat_alarm order by alarm_time desc limit 6"); }
    public List<Map<String,Object>> allAlarms() { return jdbc.queryForList("select * from heat_alarm order by alarm_time desc"); }
    public List<Map<String,Object>> monitorPoints() { return jdbc.queryForList("select * from monitor_point order by id"); }
    public Map<String,Object> pipe(Long id) { return jdbc.queryForMap("select * from heat_pipe where id=?", id); }
    public void savePipe(Long id, String code, String name, String area, String diameter, String material, String length, String pressure, String year, String status) {
        if (id == null) {
            jdbc.update("insert into heat_pipe(pipe_code,pipe_name,area,diameter,material,length_km,design_pressure,install_year,status) values(?,?,?,?,?,?,?,?,?)",
                code,name,area,diameter,material,Double.parseDouble(length),Double.parseDouble(pressure),Integer.parseInt(year),status);
        } else {
            jdbc.update("update heat_pipe set pipe_code=?,pipe_name=?,area=?,diameter=?,material=?,length_km=?,design_pressure=?,install_year=?,status=? where id=?",
                code,name,area,diameter,material,Double.parseDouble(length),Double.parseDouble(pressure),Integer.parseInt(year),status,id);
        }
    }
    public void deletePipe(Long id) { jdbc.update("delete from heat_pipe where id=?", id); }
    public void handleAlarm(Long id) { jdbc.update("update heat_alarm set status='已处理',handler='系统管理员',handle_time=NOW() where id=?", id); }
    public void updateStationStatus(Long id, String status) { jdbc.update("update heat_station set status=? where id=?",status,id); }
    public List<Map<String,Object>> recentAlarms() { return jdbc.queryForList("select * from heat_alarm order by alarm_time desc"); }
}
