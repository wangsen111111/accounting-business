package com.ywjs.jcsy.common.constant;

import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-07-31 9:56
 */
public class GlobalContstant {
    public static final List<String> IPS = Arrays.asList(new String[]{"127.0.0.1", "localhost"});
    public static final String UPLOAD_BASE_PATH = System.getProperty("user.dir") + File.separator+"file"+File.separator;
    public static final String UPLOAD_PATH = UPLOAD_BASE_PATH + "uploadFile" + File.separator;
    public static final String PREVIEW = UPLOAD_BASE_PATH + "preview" + File.separator;
    public static final String TMP_PATH = System.getProperty("user.dir") + File.separator + "tmp" + File.separator;
    // 初始化路径
    static {
        FileUtil.mkdir(UPLOAD_BASE_PATH);
        FileUtil.mkdir(UPLOAD_PATH);
        FileUtil.mkdir(PREVIEW);
        FileUtil.mkdir(TMP_PATH);
    }
}
