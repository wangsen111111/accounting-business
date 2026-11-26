package com.ywjs.jcsy.listener;

import com.ywjs.jcsy.config.ServerConfig;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;

/**
 * @author zhouwei
 * @create 2020-12-23 17:53
 */
//@Component
public class OpenBrowser implements CommandLineRunner {

    @Autowired
    ServerConfig serverConfig;

    @Override
    public void run(String... args) throws Exception {
        Runtime.getRuntime().exec("cmd   /c   start   " + serverConfig.getUrl());//可以指定自己的路径
    }
}