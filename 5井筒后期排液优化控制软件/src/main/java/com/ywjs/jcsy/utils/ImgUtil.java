package com.ywjs.jcsy.utils;

import cn.hutool.core.util.StrUtil;
import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

/**
 * @author zhouwei
 * @create 2020-12-30 11:00
 */
public class ImgUtil {

    public static boolean isPicture(String  filePath){
        // 声明图片后缀名数组
        String imgeTypes [] = {"bmp","dib","gif","jfif","jpe","jpeg","jpg","png","tif","tiff","ico"};
        List<String> imgTypes = Arrays.asList(imgeTypes);
        // 获得文件后缀名
        String fileType = filePath.substring(filePath.lastIndexOf(".") + 1,  filePath.length());
        List<String> collect = imgTypes.stream().filter(item -> item.equalsIgnoreCase(fileType)).collect(Collectors.toList());
        if(collect!=null && collect.size()>0) return true;
        else return false;
    }

    public static byte[] imageToBytes(Image image, String format) throws IOException {
        BufferedImage bImage = new BufferedImage(image.getWidth(null),
                image.getHeight(null), BufferedImage.TYPE_INT_ARGB);
        Graphics bg = bImage.getGraphics();
        bg.drawImage(image, 0, 0, null);
        bg.dispose();

        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ImageIO.write(bImage, format, out);
        return out.toByteArray();
    }

    public static byte[] imageToBytes(String path) throws IOException {
        InputStream inputStream = new FileInputStream(path);
        BufferedInputStream in = new BufferedInputStream(inputStream);
        ByteArrayOutputStream out = new ByteArrayOutputStream(1024);

        byte[] temp = new byte[1024];
        int size = 0;
        while ((size = in.read(temp)) != -1) {
            out.write(temp, 0, size);
        }
        in.close();
        byte[] content = out.toByteArray();
        return content;
    }

    //获取base64字符串
    public static String encodeBase64(byte[] data,boolean isSafe) {
        //对字节数组Base64编码
        String encodedText=null;
        if(isSafe){
            java.util.Base64.Encoder encoder = java.util.Base64.getUrlEncoder();
            encodedText = encoder.encodeToString(data);
        }else{
            BASE64Encoder encoder = new BASE64Encoder();
            encodedText=encoder.encode(data);
            encodedText=encodedText.replaceAll("[\\s*\t\n\r]", "");
        }
        return encodedText;
    }


    //获取base64字符串
    public static String encodeBase64(String filPath,boolean isSafe) {
        if(StrUtil.isBlank(filPath)){
            throw new NullPointerException();
        }
        InputStream in = null;
        byte[] data = null;

        //读取图片字节数组
        try {
            in = new BufferedInputStream(new FileInputStream(filPath));
            data = new byte[in.available()];
            in.read(data);
            in.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //对字节数组Base64编码
        return encodeBase64(data,isSafe);
    }

    //解析base64
    public static String decodeBase64(String base64,String filePath,String suffix,boolean isSafe){
        if(StrUtil.isBlank(base64)||StrUtil.isBlank(filePath)||StrUtil.isBlank(suffix)){
            throw new NullPointerException();
        }
        OutputStream out=null;
        String fileName=null;
        try {
            byte[] b=new byte[2048];
            if(isSafe){
                java.util.Base64.Decoder decoder = java.util.Base64.getUrlDecoder();
                b = decoder.decode(base64);
            }else{
                BASE64Decoder decoder = new BASE64Decoder();
                b = decoder.decodeBuffer(base64.substring(base64.indexOf(",") + 1));
            }
            for (int i = 0; i < b.length; ++i) {
                if (b[i] < 0) {// 调整异常数据
                    b[i] += 256;
                }
            }
            File file=new File(filePath);
            if(!file.exists()){
                file.mkdirs();
            }
            fileName=filePath+System.currentTimeMillis()+"."+suffix;
            out = new BufferedOutputStream(new FileOutputStream(fileName));
            out.write(b);
            out.flush();
        }catch (Exception e) {

        }finally {
            if(out!=null){
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        }
        return fileName;
    }


}
