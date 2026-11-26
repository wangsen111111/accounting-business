package com.demo.heat.controller;

import com.demo.heat.service.HeatService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

@Controller
public class SystemController {
    private final HeatService service;
    public SystemController(HeatService service) { this.service = service; }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("page","dashboard"); model.addAttribute("stats",service.dashboard());
        model.addAttribute("pipes",service.pipes()); model.addAttribute("alarms",service.latestAlarms());
        model.addAttribute("stations",service.stations()); return "dashboard";
    }
    @GetMapping("/pipes")
    public String pipes(Model model) { model.addAttribute("page","pipes"); model.addAttribute("pipes",service.pipes()); return "pipes"; }
    @GetMapping("/pipes/new")
    public String newPipe(Model model) { model.addAttribute("page","pipes"); model.addAttribute("mode","新增"); return "pipe-form"; }
    @GetMapping("/pipes/edit/{id}")
    public String editPipe(@PathVariable Long id, Model model) {
        model.addAttribute("page","pipes"); model.addAttribute("mode","编辑"); model.addAttribute("pipe",service.pipe(id)); return "pipe-form";
    }
    @PostMapping("/pipes/save")
    public String savePipe(@RequestParam(required=false) Long id, @RequestParam String code, @RequestParam String name,
        @RequestParam String area, @RequestParam String diameter, @RequestParam String material,
        @RequestParam String length, @RequestParam(defaultValue="1.60") String pressure,
        @RequestParam(defaultValue="2020") String year, @RequestParam String status, RedirectAttributes ra) {
        service.savePipe(id,code,name,area,diameter,material,length,pressure,year,status);
        ra.addFlashAttribute("message",id==null?"管线档案新增成功":"管线档案修改成功"); return "redirect:/pipes";
    }
    @PostMapping("/pipes/delete/{id}")
    public String deletePipe(@PathVariable Long id, RedirectAttributes ra) {
        service.deletePipe(id); ra.addFlashAttribute("message","管线档案已删除"); return "redirect:/pipes";
    }
    @GetMapping("/pipes/export")
    public void exportPipes(HttpServletResponse response) throws IOException {
        response.setContentType("text/csv;charset=UTF-8"); response.setHeader("Content-Disposition","attachment; filename=pipes.csv");
        response.getOutputStream().write(new byte[]{(byte)0xEF,(byte)0xBB,(byte)0xBF});
        StringBuilder sb=new StringBuilder("编号,名称,区域,管径,材质,长度(km),设计压力(MPa),建设年份,状态\r\n");
        for(java.util.Map<String,Object> p:service.pipes()) sb.append(p.get("pipe_code")).append(",").append(p.get("pipe_name")).append(",").append(p.get("area")).append(",").append(p.get("diameter")).append(",").append(p.get("material")).append(",").append(p.get("length_km")).append(",").append(p.get("design_pressure")).append(",").append(p.get("install_year")).append(",").append(p.get("status")).append("\r\n");
        response.getOutputStream().write(sb.toString().getBytes(StandardCharsets.UTF_8)); response.getOutputStream().flush();
    }
    @GetMapping("/stations")
    public String stations(Model model) { model.addAttribute("page","stations"); model.addAttribute("stations",service.stations()); return "stations"; }
    @PostMapping("/stations/status/{id}")
    public String stationStatus(@PathVariable Long id,@RequestParam String status,RedirectAttributes ra) {
        service.updateStationStatus(id,status); ra.addFlashAttribute("message","换热站状态已更新"); return "redirect:/stations";
    }
    @GetMapping("/monitor")
    public String monitor(Model model) { model.addAttribute("page","monitor"); model.addAttribute("points",service.monitorPoints()); return "monitor"; }
    @GetMapping("/alarms")
    public String alarms(Model model) { model.addAttribute("page","alarms"); model.addAttribute("alarms",service.allAlarms()); return "alarms"; }
    @PostMapping("/alarms/handle/{id}")
    public String handleAlarm(@PathVariable Long id,RedirectAttributes ra) { service.handleAlarm(id); ra.addFlashAttribute("message","报警已标记为已处理"); return "redirect:/alarms"; }
    @GetMapping("/analysis")
    public String analysis(Model model) { model.addAttribute("page","analysis"); model.addAttribute("points",service.monitorPoints()); model.addAttribute("stats",service.dashboard()); return "analysis"; }
    @GetMapping("/settings")
    public String settings(Model model) { model.addAttribute("page","settings"); return "settings"; }
}
