package com.ywjs.jcsy;

import com.ywjs.jcsy.common.handler.reequesthandler.RequestSingleParamResolver;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.util.List;

@EnableTransactionManagement
@SpringBootApplication
@MapperScan("com.ywjs.jcsy.mapper")
public class JcsyApplication extends WebMvcConfigurerAdapter {

    public static void main(String[] args) {
        SpringApplication.run(JcsyApplication.class, args);
    }
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers){
        argumentResolvers.add(new RequestSingleParamResolver());
        super.addArgumentResolvers(argumentResolvers);
    }
}
