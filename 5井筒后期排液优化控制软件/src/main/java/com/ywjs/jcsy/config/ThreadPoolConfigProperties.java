package com.ywjs.jcsy.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * @author zhouwei
 * @create 2020-10-12 23:19
 */
@Data
@Component
@ConfigurationProperties(prefix = "jcsy.thread")
public class ThreadPoolConfigProperties {
    private Integer coreSize;
    private Integer maxSize;
    private Integer keepAliveTime;
}
