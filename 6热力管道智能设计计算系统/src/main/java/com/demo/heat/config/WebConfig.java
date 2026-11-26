package com.demo.heat.config;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.*;
@Configuration
public class WebConfig implements WebMvcConfigurer {
 public void addInterceptors(InterceptorRegistry r){r.addInterceptor(new AuthInterceptor()).addPathPatterns("/**");}
}
