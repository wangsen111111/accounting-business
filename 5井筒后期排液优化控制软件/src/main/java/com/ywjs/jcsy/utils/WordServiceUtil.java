package com.ywjs.jcsy.utils;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.freemarker.FreeMarkerConfigurer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URLEncoder;

/**
 * @author zhouwei
 * @create 2020-11-06 15:03
 */
@Service
public class WordServiceUtil {

    @Autowired
    private FreeMarkerConfigurer freeMarkerConfigurer;

    public void exportWord(HttpServletRequest request, HttpServletResponse response, String fileName , String templeteName, Object dataModel){

        //创建配置实例
        Configuration configuration = freeMarkerConfigurer.getConfiguration();

        try {
            Template t=configuration.getTemplate(templeteName);

//            response.setContentType("application/msword; charset=UTF-8");// application/x-download
//            response.setHeader("Content-Disposition", "attachment; " + encodeFileName(request, fileName+".doc"));

            response.setContentType("application/text; charset=UTF-8");// application/x-download
            response.setHeader("Content-Disposition", "attachment; " + encodeFileName(request, fileName+".txt"));

            OutputStream outputStream = response.getOutputStream();
            Writer out=new OutputStreamWriter(outputStream);
            t.process(dataModel, out);
            outputStream.close();
            out.close();

        } catch (IOException | TemplateException e) {
            e.printStackTrace();
        }

    }

    public static String encodeFileName(HttpServletRequest request, String fileName) throws UnsupportedEncodingException {
        String new_filename = URLEncoder.encode(fileName, "UTF8").replaceAll("\\+", "%20");

        String agent = request.getHeader("USER-AGENT").toLowerCase();
        if (null != agent && -1 != agent.indexOf("msie"))
        {
            /**
             * IE浏览器，只能采用URLEncoder编码
             */
            return "filename=\"" + new_filename +"\"";
        }else if (null != agent && -1 != agent.indexOf("applewebkit")){
            /**
             * Chrome浏览器，只能采用ISO编码的中文输出
             */
            return "filename=\"" + new String(fileName.getBytes("UTF-8"),"ISO8859-1") +"\"";
        } else if (null != agent && -1 != agent.indexOf("opera")){
            /**
             * Opera浏览器只可以使用filename*的中文输出
             * RFC2231规定的标准
             */
            return "filename*=" + new_filename ;
        }else if (null != agent && -1 != agent.indexOf("safari")){
            /**
             * Safani浏览器，只能采用iso编码的中文输出
             */
            return "filename=\"" + new String(fileName.getBytes("UTF-8"),"ISO8859-1") +"\"";
        }else if (null != agent && -1 != agent.indexOf("firefox"))
        {
            /**
             * Firfox浏览器，可以使用filename*的中文输出
             * RFC2231规定的标准
             */
            return "filename*=" + new_filename ;
        } else
        {
            return "filename=\"" + new_filename +"\"";
        }
    }

}