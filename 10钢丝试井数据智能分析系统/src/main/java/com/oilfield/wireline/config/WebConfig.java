package com.oilfield.wireline.config;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class WebConfig implements WebMvcConfigurer {
 @Autowired AuthInterceptor auth;
 public void addInterceptors(InterceptorRegistry r){r.addInterceptor(auth).addPathPatterns("/**").excludePathPatterns("/login","/logout","/css/**","/js/**","/images/**");}
}