package com.demo.heat.controller;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import java.util.UUID;
@Controller
public class OperationsController {
 private final JdbcTemplate jdbc;
 public OperationsController(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @GetMapping("/workorders") public String orders(Model m){
  m.addAttribute("page","workorders");m.addAttribute("orders",jdbc.queryForList("select * from work_order order by created_at desc"));return "workorders";
 }
 @PostMapping("/workorders/save") public String saveOrder(@RequestParam String title,@RequestParam String area,@RequestParam String priority,@RequestParam String assignee,@RequestParam String description,RedirectAttributes ra){
  String code="WO-"+UUID.randomUUID().toString().substring(0,8).toUpperCase();
  jdbc.update("insert into work_order(order_code,title,area,priority,assignee,description,status) values(?,?,?,?,?,?,?)",code,title,area,priority,assignee,description,"待派工");
  ra.addFlashAttribute("message","维修工单创建成功");return "redirect:/workorders";
 }
 @PostMapping("/workorders/status/{id}") public String orderStatus(@PathVariable Long id,@RequestParam String status,RedirectAttributes ra){
  jdbc.update("update work_order set status=?,updated_at=NOW() where id=?",status,id);ra.addFlashAttribute("message","工单状态已更新");return "redirect:/workorders";
 }
 @GetMapping("/patrol") public String patrol(Model m){
  m.addAttribute("page","patrol");m.addAttribute("records",jdbc.queryForList("select * from patrol_record order by patrol_time desc"));
  m.addAttribute("stations",jdbc.queryForList("select station_name from heat_station order by station_name"));return "patrol";
 }
 @PostMapping("/patrol/save") public String savePatrol(@RequestParam String station,@RequestParam String inspector,@RequestParam String result,@RequestParam String note,RedirectAttributes ra){
  jdbc.update("insert into patrol_record(station_name,inspector,result,note,patrol_time) values(?,?,?,?,NOW())",station,inspector,result,note);
  ra.addFlashAttribute("message","巡检记录已保存");return "redirect:/patrol";
 }
 @GetMapping("/alarm-rules") public String rules(Model m){m.addAttribute("page","rules");m.addAttribute("rules",jdbc.queryForList("select * from alarm_rule order by id"));return "alarm-rules";}
 @PostMapping("/alarm-rules/save") public String saveRule(@RequestParam Long id,@RequestParam String rule_name,@RequestParam String metric,@RequestParam String operator,@RequestParam Double threshold_value,@RequestParam String alarm_level,@RequestParam(defaultValue="0") Integer enabled,RedirectAttributes ra){
  jdbc.update("update alarm_rule set rule_name=?,metric=?,operator=?,threshold_value=?,alarm_level=?,enabled=? where id=?",rule_name,metric,operator,threshold_value,alarm_level,enabled,id);
  ra.addFlashAttribute("message","报警规则已更新");return "redirect:/alarm-rules";
 }
 @GetMapping("/reports") public String reports(Model m){
  m.addAttribute("page","reports");
  m.addAttribute("stats",jdbc.queryForMap("select (select count(*) from heat_pipe) pipe_count,(select count(*) from heat_station) station_count,(select count(*) from heat_alarm) alarm_count,(select count(*) from work_order) order_count,(select count(*) from patrol_record) patrol_count,(select count(*) from heat_alarm where status='待处理') pending_count"));
  m.addAttribute("orders",jdbc.queryForList("select status,count(*) total from work_order group by status"));
  m.addAttribute("alarms",jdbc.queryForList("select alarm_level,count(*) total from heat_alarm group by alarm_level"));return "reports";
 }
}
