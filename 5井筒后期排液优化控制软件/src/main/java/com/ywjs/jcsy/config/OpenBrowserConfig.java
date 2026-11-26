package com.ywjs.jcsy.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import java.io.IOException;

/**
 * @author zhouwei
 * @create 2020-12-23 18:03
 */
@Configuration
public class OpenBrowserConfig {

    @Autowired
    ServerConfig serverConfig;

    @EventListener({ApplicationReadyEvent.class})
    void applicationReadyEvent() {
        try {
            Runtime.getRuntime().exec("cmd   /c   start   " + serverConfig.getUrl());//可以指定自己的路径
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
