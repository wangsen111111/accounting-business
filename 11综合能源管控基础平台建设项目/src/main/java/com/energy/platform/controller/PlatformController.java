package com.energy.platform.controller;

import com.energy.platform.service.EnergyService;
import java.util.*;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PlatformController {
    @Autowired private EnergyService service;

    @GetMapping("/login") public String login() { return "login"; }
    @PostMapping("/login") public String doLogin(@RequestParam String username, @RequestParam String password, HttpSession session, Model model) {
        if (("admin".equals(username) && "admin123".equals(password)) || ("operator".equals(username) && "operator123".equals(password)) || ("viewer".equals(username) && "viewer123".equals(password))) {
            session.setAttribute("loginUser", username); session.setAttribute("role", "admin".equals(username) ? "系统管理员" : ("operator".equals(username) ? "运维人员" : "只读用户")); return "redirect:/dashboard";
        }
        model.addAttribute("error", "用户名或密码错误"); return "login";
    }
    @GetMapping("/logout") public String logout(HttpSession session) { session.invalidate(); return "redirect:/login"; }
    @GetMapping({"/", "/dashboard"}) public String dashboard(Model model, HttpSession session) { fill(model, "dashboard", "综合能源驾驶舱", session); return "dashboard"; }
    @GetMapping("/{page:devices|analytics|telemetry|collection|alarms|workorders|campuses|topology|carbon|tariffs|reports|integration|audit|settings|users}" )
    public String page(@PathVariable String page, Model model, HttpSession session) {
        Map<String,String> titles = new HashMap<>();
        titles.put("devices","能源设备台账"); titles.put("analytics","能源统计分析"); titles.put("telemetry","实时监测中心"); titles.put("collection","数据采集与治理"); titles.put("alarms","告警管理中心"); titles.put("workorders","运维工单中心"); titles.put("campuses","园区综合管理"); titles.put("topology","能源系统拓扑"); titles.put("carbon","双碳分析中心"); titles.put("tariffs","能源价格管理"); titles.put("reports","能源报表中心"); titles.put("integration","系统集成中心"); titles.put("audit","运行审计日志"); titles.put("settings","平台系统设置"); titles.put("users","用户与权限管理");
        fill(model, page, titles.get(page), session); return "dashboard";
    }
    private void fill(Model model, String page, String title, HttpSession session) {
        model.addAttribute("page", page); model.addAttribute("pageTitle", title); model.addAttribute("username", session.getAttribute("loginUser")); model.addAttribute("role", session.getAttribute("role"));
        model.addAttribute("devices", service.devices()); model.addAttribute("alarms", service.alarms()); model.addAttribute("orders", service.orders());
        model.addAttribute("deviceCount", service.countDevices()); model.addAttribute("onlineCount", service.countOnline()); model.addAttribute("onlinePercent", service.countDevices() == 0 ? 0 : (service.countOnline() * 100 / service.countDevices())); model.addAttribute("alarmCount", service.countOpenAlarms()); model.addAttribute("orderCount", service.countOrders());
        model.addAttribute("power", "1,248.6"); model.addAttribute("water", "3,862"); model.addAttribute("gas", "8,421"); model.addAttribute("energyTrend", Arrays.asList(42,55,48,68,61,75,70,88,79,92,83,96));
    }
    @PostMapping("/devices/add") public String addDevice(@RequestParam String deviceCode,@RequestParam String deviceName,@RequestParam String energyType,@RequestParam String location,@RequestParam String unit) { service.addDevice(deviceCode,deviceName,energyType,location,unit); return "redirect:/devices"; }
    @PostMapping("/alarms/{id}/ack") public String ack(@PathVariable long id) { service.ackAlarm(id); return "redirect:/alarms"; }
    @PostMapping("/workorders/add") public String addOrder(@RequestParam String title,@RequestParam String deviceName,@RequestParam String assignee,@RequestParam String priority) { service.addOrder(title,deviceName,assignee,priority); return "redirect:/workorders"; }
    @PostMapping("/workorders/{id}/finish") public String finish(@PathVariable long id) { service.finishOrder(id); return "redirect:/workorders"; }
    @GetMapping("/reports/export") public void export(HttpServletResponse response) throws Exception {
        response.setContentType("text/csv;charset=UTF-8"); response.setHeader("Content-Disposition", "attachment; filename=energy-devices.csv");
        response.getWriter().write("\uFEFF设备编号,设备名称,能源类型,位置,状态,当前读数,单位\r\n");
        for (Map<String,Object> d : service.devices()) response.getWriter().write(d.get("device_code")+","+d.get("device_name")+","+d.get("energy_type")+","+d.get("location")+","+d.get("status")+","+d.get("metric_value")+","+d.get("unit")+"\r\n");
    }
}
