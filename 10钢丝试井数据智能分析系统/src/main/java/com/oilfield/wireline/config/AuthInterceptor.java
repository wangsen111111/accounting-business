package com.oilfield.wireline.config;
import javax.servlet.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
@Component
public class AuthInterceptor implements HandlerInterceptor {
 public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler)throws Exception{
  String p=req.getRequestURI();
  if(p.equals("/login")||p.startsWith("/css/")||p.startsWith("/js/")||p.startsWith("/images/")) return true;
  if(req.getSession().getAttribute("user")!=null)return true;
  res.sendRedirect("/login"); return false;
 }
}