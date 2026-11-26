package com.energy.platform.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.HandlerInterceptor;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new HandlerInterceptor() {
            @Override public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
                String uri = request.getRequestURI();
                if (uri.equals("/login") || uri.startsWith("/css/") || uri.startsWith("/js/") || uri.equals("/favicon.ico")) return true;
                HttpSession session = request.getSession(false);
                if (session == null || session.getAttribute("loginUser") == null) { response.sendRedirect("/login"); return false; }
                return true;
            }
        }).addPathPatterns("/**");
    }
}
