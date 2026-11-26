package com.ywjs.jcsy.config;

import cn.hutool.core.io.FileUtil;
import com.ywjs.jcsy.common.constant.GlobalContstant;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * 设置虚拟路径，访问绝对路径下资源
 * @author zhouwei
 * @create 2020-08-27 14:30
 *
 */
@Configuration
public class UploadFilePathConfig implements WebMvcConfigurer{
    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // 初始化服务路径
        if(!FileUtil.exist(GlobalContstant.UPLOAD_BASE_PATH)){
            FileUtil.mkdir(GlobalContstant.UPLOAD_BASE_PATH);
        }
        registry.addResourceHandler(staticAccessPath).addResourceLocations("file:" + GlobalContstant.UPLOAD_BASE_PATH);
    }

}