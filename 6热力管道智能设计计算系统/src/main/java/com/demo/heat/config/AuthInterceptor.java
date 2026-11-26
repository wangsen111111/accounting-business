package com.demo.heat.config;
import javax.servlet.http.*;
import org.springframework.web.servlet.HandlerInterceptor;
public class AuthInterceptor implements HandlerInterceptor {
 public boolean preHandle(HttpServletRequest req,HttpServletResponse res,Object handler)throws Exception{
  String u=req.getRequestURI();
  if(u.startsWith("/login")||u.startsWith("/css/")||u.startsWith("/js/")||u.equals("/favicon.ico"))return true;
  HttpSession s=req.getSession(false);
  if(s!=null&&s.getAttribute("loginUser")!=null)return true;
  res.sendRedirect(req.getContextPath()+"/login"); return false;
 }
}
