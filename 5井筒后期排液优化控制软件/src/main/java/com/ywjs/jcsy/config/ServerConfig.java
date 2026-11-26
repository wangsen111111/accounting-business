package com.ywjs.jcsy.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.context.WebServerInitializedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.net.UnknownHostException;
/**
 * @author zhouwei
 * @create 2020-09-01 16:34
 */
@Component
public class ServerConfig implements ApplicationListener<WebServerInitializedEvent> {
    public int serverPort;

    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    public String getUrl() {
        InetAddress address = null;
        try {
            address = InetAddress.getLocalHost();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
//        return "http://"+address.getHostAddress() +":"+this.serverPort;
        return "http://127.0.0.1:"+this.serverPort;
    }

    public String getAccessPath(String fileName){
        staticAccessPath = staticAccessPath.replace("*", "");
        return getUrl()+staticAccessPath+fileName;
    }

    @Override
    public void onApplicationEvent(WebServerInitializedEvent event) {
        this.serverPort = event.getWebServer().getPort();
    }

}
