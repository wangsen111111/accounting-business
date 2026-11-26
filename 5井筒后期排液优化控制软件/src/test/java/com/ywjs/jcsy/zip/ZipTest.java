package com.ywjs.jcsy.zip;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ZipUtil;
import org.junit.Test;

import java.io.File;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

//import org.junit.jupiter.api.Test;

/**
 * @author zhouwei
 * @create 2020-08-03 17:48
 */
public class ZipTest {

    @Test
    public void testzip(){
        System.out.println("获取操作系统默认字符编码的方法："+System.getProperties().get("sun.jnu.encoding"));
        System.out.println("获取操作系统文件的字符编码的方法："+System.getProperties().get("file.encoding"));
        System.out.println("获取JVM默认字符编码的方法："+Charset.defaultCharset());

        Charset defaultCharset = Charset.defaultCharset();
        System.out.println("Default Charset: " + defaultCharset.displayName());
        System.out.println();

//        SortedMap<String, Charset> charsets = Charset.availableCharsets();
//        Set<String> keys = charsets.keySet();
//        for (String key : keys) {
//            Charset charset = charsets.get(key);
//            String charsetName = charset.displayName();
//            System.out.println(key + ": " + charsetName);}
//        String src = "E:\\谷歌浏览器下载\\13年.zip";
//        ZipUtil.unzip(zipPaht);
//        ZipUtil.unzip(src);
//        ZipUtil.unzip(src , Charset.forName("gbk"));
//        ZipUtil.unzip(src , Charset.forName("UTF-8"));
    }

    @Test
    public void test(){
        ZipUtil.unzip("E:\\test\\试验.zip","E:\\test\\zip" , Charset.forName("GBK"));
        File file = new File("E:\\test\\");
        System.out.println(file.getName());
        System.out.println(file.getParent());
        System.out.println(file.getAbsolutePath());
        List<String> currNodeFilePaths = new ArrayList<>();
        currNodeFilePaths.add("E:\\工作文件\\工作文件\\井场试验全流程知识管理平台（前端）\\测试用例\\OpenwellsProject\\OpenWells可识别的Xml文件\\Dul-112.edm.xml");
        currNodeFilePaths.add("E:\\工作文件\\工作文件\\井场试验全流程知识管理平台（前端）\\测试用例\\OpenwellsProject\\OpenWells可识别的Xml文件\\S区块.edm.xml");

        currNodeFilePaths.forEach(currNodeFilePath-> FileUtil.copy(currNodeFilePath,"E:\\workspace\\jcsy\\tmp\\OpenwellsProject\\OpenWells可识别的Xml文件",false));

        String currProjectPath = System.getProperty("user.dir") + "/tmp";
        ZipUtil.zip(currProjectPath, System.getProperty("user.dir")+"/zip/项目压缩包.zip", true);
    }
}
