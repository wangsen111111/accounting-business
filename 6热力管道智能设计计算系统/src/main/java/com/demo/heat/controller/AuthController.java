package com.demo.heat.controller;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpSession;
@Controller
public class AuthController {
 private final JdbcTemplate jdbc;
 public AuthController(JdbcTemplate jdbc){this.jdbc=jdbc;}
 @GetMapping("/login") public String login(){return "login";}
 @PostMapping("/login") public String doLogin(@RequestParam String username,@RequestParam String password,HttpSession s,Model m){
  Integer n=jdbc.queryForObject("select count(*) from sys_user where username=? and password=? and enabled=1",Integer.class,username.trim(),password);
  if(n!=null&&n>0){s.setAttribute("loginUser",username.trim());return "redirect:/";}
  m.addAttribute("error","账号或密码错误，或账户已停用");return "login";
 }
 @GetMapping("/logout") public String logout(HttpSession s){s.invalidate();return "redirect:/login";}
}
