package com.ywjs.jcsy.utils;

import cn.hutool.core.io.FileUtil;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.net.URLEncoder;

/**
 * @author zhouwei
 * @create 2020-08-03 16:39
 */
public class FileDownloadUtil {

    /**
     * 浏览器下载文件
     * @param response
     * @param filePath 文件路径
     * @param fileName 下载文件名
     */
    public static void fileDownload(HttpServletResponse response, String filePath, String fileName,String delPath) {
        try (OutputStream os = response.getOutputStream();
             FileInputStream fis = new FileInputStream(filePath)) {
            response.setHeader("content-type", "application/octet-stream");
            response.setHeader("Content-Disposition", "attachment;filename="  + URLEncoder.encode(fileName, "UTF-8"));
            int len = 0;
            byte[] bt = new byte[5 * 1024];
            while ((len = fis.read(bt)) != -1) {
                os.write(bt, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            // 删除临时目录,而不删除目录
            FileUtil.del(delPath);
//            File[] ls = FileUtil.ls(GlobalContstant.TMP_PATH);
//            for (File file : ls) { FileUtil.del(file); }
            // 删除临时目录
//            FileUtil.del(GlobalContstant.TMP_PATH);
        }
    }
}
