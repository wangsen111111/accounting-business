package com.oilfield.wireline.controller;
import java.util.*;
import javax.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.io.*;
@Controller
public class MainController {
 @Autowired JdbcTemplate db;
 @GetMapping("/login") public String login(){return "login";}
 @PostMapping("/login") public String doLogin(@RequestParam String username,@RequestParam String password,HttpSession s,Model m){
  if("admin".equals(username)&&"admin123".equals(password)){s.setAttribute("user",username);return "redirect:/";}
  m.addAttribute("error","账号或密码错误");return "login";
 }
 @GetMapping("/logout") public String logout(HttpSession s){s.invalidate();return "redirect:/login";}
 @ModelAttribute public void common(Model m,HttpSession s){m.addAttribute("loginUser",s.getAttribute("user"));}
 @GetMapping("/") public String dashboard(Model m){
  m.addAttribute("wellCount",scalar("select count(*) from well_info"));
  m.addAttribute("testCount",scalar("select count(*) from test_job"));
  m.addAttribute("alertCount",scalar("select count(*) from analysis_alert where status='待处理'"));
  m.addAttribute("avgDepth",scalar("select ifnull(round(avg(max_depth),1),0) from test_job"));
  m.addAttribute("recent",db.queryForList("select t.*,w.well_name from test_job t left join well_info w on t.well_id=w.id order by t.id desc limit 7"));
  m.addAttribute("alerts",db.queryForList("select * from analysis_alert order by id desc limit 5"));
  return "dashboard";
 }
 private int scalar(String sql){Integer n=db.queryForObject(sql,Integer.class);return n==null?0:n;}
 @GetMapping("/wells") public String wells(Model m){m.addAttribute("rows",db.queryForList("select * from well_info order by id desc"));return "wells";}
 @PostMapping("/wells/save") public String saveWell(@RequestParam(required=false) Long id,@RequestParam String wellCode,@RequestParam String wellName,@RequestParam(required=false) String blockName,@RequestParam(required=false) String wellType,@RequestParam(required=false) String location,@RequestParam(required=false) String status){
  if(id==null)db.update("insert into well_info(well_code,well_name,block_name,well_type,location,status) values(?,?,?,?,?,?)",wellCode,wellName,blockName,wellType,location,status);
  else db.update("update well_info set well_code=?,well_name=?,block_name=?,well_type=?,location=?,status=? where id=?",wellCode,wellName,blockName,wellType,location,status,id);
  return "redirect:/wells";
 }
 @GetMapping("/wells/delete/{id}") public String deleteWell(@PathVariable long id){db.update("delete from well_info where id=?",id);return "redirect:/wells";}
 @GetMapping("/tests") public String tests(Model m){m.addAttribute("rows",db.queryForList("select t.*,w.well_name from test_job t left join well_info w on t.well_id=w.id order by t.id desc"));m.addAttribute("wells",db.queryForList("select * from well_info"));return "tests";}
 @PostMapping("/tests/save") public String saveTest(@RequestParam(required=false) Long id,@RequestParam long wellId,@RequestParam String testNo,@RequestParam String testDate,@RequestParam(required=false) String operator,@RequestParam(required=false) String toolModel,@RequestParam(required=false) Double maxDepth,@RequestParam(required=false) Double cableTension,@RequestParam(required=false) String conclusion){
  if(id==null)db.update("insert into test_job(well_id,test_no,test_date,operator,tool_model,max_depth,cable_tension,conclusion) values(?,?,?,?,?,?,?,?)",wellId,testNo,testDate,operator,toolModel,maxDepth,cableTension,conclusion);
  else db.update("update test_job set well_id=?,test_no=?,test_date=?,operator=?,tool_model=?,max_depth=?,cable_tension=?,conclusion=? where id=?",wellId,testNo,testDate,operator,toolModel,maxDepth,cableTension,conclusion,id);
  return "redirect:/tests";
 }
 @GetMapping("/tests/delete/{id}") public String deleteTest(@PathVariable long id){db.update("delete from test_job where id=?",id);return "redirect:/tests";}
 @GetMapping("/analysis") public String analysis(Model m){
  m.addAttribute("rows",db.queryForList("select t.*,w.well_name from test_job t left join well_info w on t.well_id=w.id order by t.id desc"));
  m.addAttribute("alerts",db.queryForList("select * from analysis_alert order by id desc"));
  return "analysis";
 }
 @PostMapping("/analysis/run") public String runAnalysis(){
  List<Map<String,Object>> rows=db.queryForList("select * from test_job");
  for(Map<String,Object> r:rows){
   double tension=r.get("cable_tension")==null?0:((Number)r.get("cable_tension")).doubleValue();
   String level=tension>18?"高风险":(tension>12?"关注":"正常");
   String msg=tension>18?"钢丝张力超过18kN参考阈值，建议核查遇阻、摩阻及设备状态":(tension>12?"钢丝张力超过12kN关注阈值，建议结合曲线复核":"任务张力参数处于当前规则参考范围");
   if(tension>12) db.update("insert into analysis_alert(test_id,alert_level,alert_title,alert_detail,status,created_at) values(?,?,?,?,?,now())",r.get("id"),level,"任务张力异常提示",msg,"待处理");
   db.update("update test_job set analysis_result=?,risk_level=? where id=?",msg,level,r.get("id"));
  }
  // 对采集点执行辅助异常扫描：张力、速度、压力均使用可配置的演示参考阈值
  List<Map<String,Object>> samples=db.queryForList("select * from wireline_data order by id desc limit 1000");
  for(Map<String,Object> p:samples){
   double t=p.get("tension")==null?0:((Number)p.get("tension")).doubleValue();
   double v=p.get("speed")==null?0:((Number)p.get("speed")).doubleValue();
   double pressure=p.get("pressure_value")==null?0:((Number)p.get("pressure_value")).doubleValue();
   String detail=null,level="关注",title=null;
   if(t>18){title="采集点张力高风险";detail="采集点张力超过18kN参考阈值，请复核作业工况。";level="高风险";}
   else if(t>12){title="采集点张力偏高";detail="采集点张力超过12kN关注阈值，请结合曲线复核。";}
   else if(v>30){title="下放速度异常提示";detail="采集点速度超过30m/min演示阈值，请核对设备及采集数据。";}
   else if(pressure>35){title="压力异常提示";detail="采集点压力超过35MPa演示阈值，请按现场标准核验。";}
   if(detail!=null) db.update("insert into analysis_alert(test_id,alert_level,alert_title,alert_detail,status,created_at) values(?,?,?,?,?,now())",null,level,title,detail,"待处理");
  }
  return "redirect:/analysis";
 }
 @GetMapping("/alerts/resolve/{id}") public String resolve(@PathVariable long id){db.update("update analysis_alert set status='已处理' where id=?",id);return "redirect:/analysis";}
 @GetMapping("/data") public String data(Model m){m.addAttribute("rows",db.queryForList("select d.*,w.well_name from wireline_data d left join well_info w on d.well_id=w.id order by d.id desc limit 300"));m.addAttribute("wells",db.queryForList("select * from well_info"));return "data";}
 @PostMapping("/data/save") public String saveData(@RequestParam long wellId,@RequestParam String sampleTime,@RequestParam Double depth,@RequestParam(required=false) Double tension,@RequestParam(required=false) Double speed,@RequestParam(required=false) Double pressure){
  db.update("insert into wireline_data(well_id,sample_time,depth,tension,speed,pressure_value) values(?,?,?,?,?,?)",wellId,sampleTime,depth,tension,speed,pressure);return "redirect:/data";
 }
 @PostMapping("/data/import") public String importCsv(@RequestParam MultipartFile file,@RequestParam long wellId)throws Exception{
  BufferedReader br=new BufferedReader(new InputStreamReader(file.getInputStream(),"UTF-8"));String line;boolean first=true;
  while((line=br.readLine())!=null){if(first){first=false;continue;}String[] a=line.split(",");if(a.length>=3)try{db.update("insert into wireline_data(well_id,sample_time,depth,tension,speed,pressure_value) values(?,?,?,?,?,?)",wellId,a[0],Double.parseDouble(a[1]),Double.parseDouble(a[2]),a.length>3?Double.parseDouble(a[3]):null,a.length>4?Double.parseDouble(a[4]):null);}catch(Exception ignored){}}
  br.close();return "redirect:/data";
 }
 @GetMapping("/reports") public String reports(Model m){m.addAttribute("rows",db.queryForList("select t.*,w.well_name from test_job t left join well_info w on t.well_id=w.id order by t.id desc"));return "reports";}
 @GetMapping("/settings") public String settings(Model m){m.addAttribute("rules",db.queryForList("select * from analysis_rule order by id"));return "settings";}
 @PostMapping("/settings/rule") public String rule(@RequestParam long id,@RequestParam double threshold){db.update("update analysis_rule set threshold_value=? where id=?",threshold,id);return "redirect:/settings";}

 @GetMapping("/curves") public String curves(Model m){
  m.addAttribute("wells",db.queryForList("select * from well_info order by well_name"));
  m.addAttribute("wellCount",scalar("select count(*) from well_info"));
  m.addAttribute("sampleCount",scalar("select count(*) from wireline_data"));
  return "curves";
 }
 @GetMapping("/api/curve-data") @ResponseBody public List<Map<String,Object>> curveData(@RequestParam(required=false) Long wellId){
  if(wellId==null)return db.queryForList("select d.sample_time,d.depth,d.tension,d.speed,d.pressure_value from wireline_data d order by d.id desc limit 300");
  return db.queryForList("select d.sample_time,d.depth,d.tension,d.speed,d.pressure_value from wireline_data d where d.well_id=? order by d.id desc limit 300",wellId);
 }
 @GetMapping("/compare") public String compare(Model m){
  m.addAttribute("rows",db.queryForList("select w.well_name,count(t.id) task_count,round(avg(t.max_depth),1) avg_depth,round(max(t.max_depth),1) max_depth,round(avg(t.cable_tension),2) avg_tension from well_info w left join test_job t on t.well_id=w.id group by w.id,w.well_name order by task_count desc"));
  return "compare";
 }
 @GetMapping("/reports/export") public void exportReport(javax.servlet.http.HttpServletResponse response)throws Exception{
  response.setContentType("text/csv;charset=UTF-8");
  response.setHeader("Content-Disposition","attachment;filename=wireline-report.csv");
  java.io.PrintWriter out=response.getWriter();
  out.write("\uFEFF任务编号,井名称,测试日期,作业人员,工具型号,最大深度(m),张力(kN),风险等级,分析结论\r\n");
  List<Map<String,Object>> rows=db.queryForList("select t.*,w.well_name from test_job t left join well_info w on t.well_id=w.id order by t.id desc");
  for(Map<String,Object> r:rows){out.println(csv(r.get("test_no"))+","+csv(r.get("well_name"))+","+csv(r.get("test_date"))+","+csv(r.get("operator"))+","+csv(r.get("tool_model"))+","+csv(r.get("max_depth"))+","+csv(r.get("cable_tension"))+","+csv(r.get("risk_level"))+","+csv(r.get("analysis_result")==null?r.get("conclusion"):r.get("analysis_result")));}
  out.flush();
 }
 private String csv(Object v){String x=v==null?"":String.valueOf(v);return "\""+x.replace("\"","\"\"")+"\"";}

 @GetMapping("/diagnosis") public String diagnosis(Model m){
  m.addAttribute("wells",db.queryForList("select * from well_info order by well_name"));
  m.addAttribute("events",db.queryForList("select a.*,w.well_name from analysis_alert a left join test_job t on a.test_id=t.id left join well_info w on t.well_id=w.id order by a.id desc limit 200"));
  m.addAttribute("sampleCount",scalar("select count(*) from wireline_data"));
  return "diagnosis";
 }
 @PostMapping("/diagnosis/run") public String runDiagnosis(@RequestParam(required=false) Long wellId){
  String sql="select * from wireline_data "+(wellId==null?"":"where well_id=? ")+"order by well_id,sample_time,id";
  List<Map<String,Object>> points=wellId==null?db.queryForList(sql):db.queryForList(sql,wellId);
  Map<Long,Map<String,Object>> previous=new java.util.HashMap<Long,Map<String,Object>>();
  for(Map<String,Object> p:points){
   Long wid=((Number)p.get("well_id")).longValue();
   double tension=num(p.get("tension")),speed=num(p.get("speed")),depth=num(p.get("depth"));
   String title=null,detail=null,level="关注";
   Map<String,Object> prev=previous.get(wid);
   double jump=prev==null?0:Math.abs(tension-num(prev.get("tension")));
   double depthDelta=prev==null?0:Math.abs(depth-num(prev.get("depth")));
   if(tension>18){title="钢丝张力高风险";detail="当前采集点张力 "+tension+" kN，超过18kN演示阈值；建议核查井下阻力、摩擦及地面设备。";level="高风险";}
   else if(prev!=null && jump>4 && depthDelta<30){title="疑似遇阻/张力突变";detail="相邻采集点张力变化 "+jump+" kN，而深度变化较小；可能存在遇阻、摩擦变化或采集异常，请结合现场工况复核。";level="高风险";}
   else if(prev!=null && num(prev.get("depth"))-depth>10){title="深度回退异常";detail="当前深度较上一采集点回退超过10m，请核查深度编码器、采集顺序及作业记录。";}
   else if(speed>30){title="下放速度偏高";detail="当前速度 "+speed+" m/min，超过30m/min演示阈值，请核查操作规程与设备状态。";}
   else if(num(p.get("pressure_value"))>35){title="压力异常提示";detail="当前压力超过35MPa演示阈值，请按井况和企业标准复核。";}
   if(title!=null){
    db.update("insert into analysis_alert(test_id,alert_level,alert_title,alert_detail,status,created_at) values(?,?,?,?,?,now())",null,level,title,detail,"待处理");
    db.update("insert into operation_log(action_type,action_detail,operator_name,created_at) values(?,?,?,now())","智能诊断",title+"："+detail,"admin");
   }
   previous.put(wid,p);
  }
  return "redirect:/diagnosis";
 }
 private double num(Object x){return x==null?0:((Number)x).doubleValue();}
 @GetMapping("/api/dashboard") @ResponseBody public Map<String,Object> dashboardApi(){
  Map<String,Object> result=new java.util.LinkedHashMap<String,Object>();
  result.put("daily",db.queryForList("select test_date label,count(*) value from test_job group by test_date order by test_date desc limit 7"));
  result.put("risk",db.queryForList("select alert_level label,count(*) value from analysis_alert group by alert_level"));
  result.put("samples",db.queryForList("select sample_time,tension,depth,speed,pressure_value from wireline_data order by id desc limit 60"));
  return result;
 }
 @GetMapping("/logs") public String logs(Model m){m.addAttribute("rows",db.queryForList("select * from operation_log order by id desc limit 300"));return "logs";}
}
