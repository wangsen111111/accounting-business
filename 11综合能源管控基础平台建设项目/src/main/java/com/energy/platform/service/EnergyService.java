package com.energy.platform.service;

import java.util.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;

@Service
public class EnergyService {
    @Autowired private JdbcTemplate jdbc;
    public List<Map<String,Object>> devices(){ return jdbc.queryForList("SELECT * FROM energy_device ORDER BY id DESC"); }
    public List<Map<String,Object>> alarms(){ return jdbc.queryForList("SELECT * FROM energy_alarm ORDER BY id DESC"); }
    public List<Map<String,Object>> orders(){ return jdbc.queryForList("SELECT * FROM energy_work_order ORDER BY id DESC"); }
    public int countDevices(){ return jdbc.queryForObject("SELECT COUNT(*) FROM energy_device",Integer.class); }
    public int countOnline(){ return jdbc.queryForObject("SELECT COUNT(*) FROM energy_device WHERE status='在线'",Integer.class); }
    public int countOpenAlarms(){ return jdbc.queryForObject("SELECT COUNT(*) FROM energy_alarm WHERE alarm_status<>'已处理'",Integer.class); }
    public int countOrders(){ return jdbc.queryForObject("SELECT COUNT(*) FROM energy_work_order",Integer.class); }
    public void addDevice(String code,String name,String type,String location,String unit){ jdbc.update("INSERT INTO energy_device(device_code,device_name,energy_type,location,status,metric_value,unit) VALUES(?,?,?,?,?,?,?)",code,name,type,location,"在线",0,unit); }
    public void ackAlarm(long id){ jdbc.update("UPDATE energy_alarm SET alarm_status='已处理' WHERE id=?",id); }
    public void addOrder(String title,String device,String assignee,String priority){ jdbc.update("INSERT INTO energy_work_order(order_no,title,device_name,assignee,priority,status) VALUES(?,?,?,?,?,?)","WO"+System.currentTimeMillis(),title,device,assignee,priority,"待接单"); }
    public void finishOrder(long id){ jdbc.update("UPDATE energy_work_order SET status='已完成' WHERE id=?",id); }
}
