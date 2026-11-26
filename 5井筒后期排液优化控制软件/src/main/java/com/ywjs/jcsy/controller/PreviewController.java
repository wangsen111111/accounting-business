package com.ywjs.jcsy.controller;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IORuntimeException;
import com.ywjs.jcsy.common.annotation.RequestSingleParam;
import com.ywjs.jcsy.common.constant.GlobalContstant;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.common.result.ResultCode;
import com.ywjs.jcsy.config.ServerConfig;
import com.ywjs.jcsy.exception.GlobalException;
import com.ywjs.jcsy.utils.ImgUtil;
import com.ywjs.jcsy.utils.PoiUtils;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-08-27 10:45
 */
@CrossOrigin
@RestController
@RequestMapping("/preview")
@Api(value="预览controller",tags={"预览接口"})
public class PreviewController {

    @Value("${file.staticAccessPath}")
    private String staticAccessPath;
    @Value("${server.port}")
    private String port;

    @Autowired
    ServerConfig serverConfig;

    @PostMapping("/wordToHtml")
    public Result wordToHtml(@RequestSingleParam("param") String wordPath) throws Exception {
        // 校验文件路径
        if(!FileUtil.exist(wordPath)){
            throw new GlobalException(ResultCode.FILE_NOT_EXIST_ERROR);
        }
        File file = new File(wordPath);
        String fileName = file.getName();
        String path = file.getParent()+"\\";
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
        String html = "";
        if(fileType.equalsIgnoreCase("docx")){
            html = PoiUtils.wordToDocx(wordPath);
        }
        if(fileType.equalsIgnoreCase("doc")){
            html = PoiUtils.wordToDoc(path,fileName);
        }
        if(fileType.equalsIgnoreCase("xls") || fileType.equalsIgnoreCase("xlsx")){
            html = PoiUtils.excelToHtml(wordPath);
        }
        if(fileType.equalsIgnoreCase("txt")){
            html = PoiUtils.txtToStr(wordPath);
        }
        return Result.ok().data(html);
    }

    @PostMapping("/serviceUrl")
    public Result getServiceUrl(@RequestSingleParam("param") String filePath)  {
//         判断是否为图片，
        if( ImgUtil.isPicture(filePath)){
            String encodeBase64 = ImgUtil.encodeBase64(filePath, false);
            return Result.ok().data(encodeBase64);
        }
        // 获取文件名称
        String fileName = new File(filePath).getName();
        // 将文件赋值到到资源路径下
        try {
            FileUtil.copy(filePath,GlobalContstant.PREVIEW,true);
        } catch (IORuntimeException e) {
            throw new GlobalException(ResultCode.FILE_NOT_EXIST_ERROR);
        }
        // 拼接服务器资源路径
        staticAccessPath = staticAccessPath.replace("*", "");
        StringBuffer sb = new StringBuffer();
        sb.append(serverConfig.getUrl()).append(staticAccessPath).append("preview/").append(fileName);
//        sb.append("http://127.0.0.1").append(":").append(port).append(staticAccessPath).append("preview/").append(fileName);
        return Result.ok().data(sb.toString());
    }

    @PostMapping("/getPPTServiceUrl")
    public Result getPPTServiceUrl(@RequestSingleParam("param") String filePath) throws Exception {
        // 拼接服务器资源路径
        staticAccessPath = staticAccessPath.replace("*", "");
        StringBuffer sb = new StringBuffer();
        sb.append(serverConfig.getUrl()).append(staticAccessPath);
        // 获取文件类型
        File file = new File(filePath);
        String fileName = file.getName();
        String fileType = fileName.substring(fileName.lastIndexOf(".")+1, fileName.length());
        List<String> serviecePaths = null;
        if(fileType.equalsIgnoreCase("ppt")){
            serviecePaths = PoiUtils.doPPTtoImage(filePath, sb.toString());
        }
        if(fileType.equalsIgnoreCase("pptx")){
            serviecePaths = PoiUtils.doPPTXtoImage(filePath, sb.toString());
        }

        return Result.ok().data(serviecePaths);
    }


}
