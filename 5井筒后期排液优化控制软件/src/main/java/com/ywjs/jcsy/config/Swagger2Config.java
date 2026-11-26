package com.ywjs.jcsy.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spring.web.plugins.Docket;
import springfox.documentation.swagger2.annotations.EnableSwagger2;


@Configuration
@EnableSwagger2
public class Swagger2Config {


    /**
     * 通过 createRestApi函数来构建一个DocketBean
     * 函数名,可以随意命名,喜欢什么命名就什么命名
     */
    @Bean
    public Docket webApiConfig(){

        return new Docket(DocumentationType.SWAGGER_2)
                .groupName("webApi")
                .apiInfo(webApiInfo())
                .select()
                .build();

    }


    /**
     *
     * - swagger.title=标题
     * - swagger.description=描述
     * - swagger.version=版本
     * - swagger.license=许可证
     * - swagger.licenseUrl=许可证URL
     * - swagger.termsOfServiceUrl=服务条款URL
     * - swagger.contact.name=维护人
     * - swagger.contact.url=维护人URL
     * - swagger.contact.email=维护人email
     * - swagger.base-package=swagger扫描的基础包，默认：全扫描
     * - swagger.base-path=需要处理的基础URL规则，默认：/**
     * - swagger.exclude-path=需要排除的URL规则，默认：空
     * - swagger.host=文档的host信息，默认：空
     */
    //构建 api文档的详细信息函数
    private ApiInfo webApiInfo(){

        return new ApiInfoBuilder()
                .title("井场试验全流程管理平台")
                .description("接口文档")
                .version("1.0")
//                .contact(new Contact("lipengcheng", "http://www.ywjs.com", "595204417@qq.com"))
//                .contact("lipengcheng")
                .build();
    }

}
