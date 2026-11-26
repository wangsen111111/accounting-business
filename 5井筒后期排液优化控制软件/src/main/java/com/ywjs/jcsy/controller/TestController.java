package com.ywjs.jcsy.controller;

import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.utils.WordServiceUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * @author zhouwei
 * @create 2020-07-29 18:28
 */
@CrossOrigin
@RestController
@RequestMapping("/test")
@Api(value="测试controller",tags={"测试接口"})
public class TestController {

    @ApiOperation(value="返回测试数据",tags={},notes="")
    @GetMapping("test")
    public Result test(){
        return new Result().data("测试");
    }

    @ApiOperation(value="返回测试数据2",tags={},notes="")
    @GetMapping("test2")
    public Result test2(){
        return new Result().data("测试");
    }

    @ApiOperation(value = "測試文件下載", tags = {}, notes = "思路：若该项目为外链形式构成，创建临时目录以思维导图结构将文件组织为一个结构化目录，最后将该目录打包成压缩文件以供下载，最后删除临时文件；否则直接打包下载")
//    @PostMapping("output")
    @GetMapping("output")
    public void outputProject(HttpServletResponse response) { //@RequestBody List<Long> ids
        System.out.println("pppppp");
        // 浏览器下载
//        FileDownloadUtil.fileDownload(response, "E:\\workspace\\jcsy\\pom.xml", "pom.xml");
    }

    @PostMapping("test")
    public Result test(@RequestBody Jsmind jsmind) { //@RequestBody List<Long> ids
//        Jsmind jsmind = new Jsmind().setData(data).setEditable(false).setSupportHtml(false);
        jsmind.insert();
        System.out.println(jsmind);
        return Result.ok().data(jsmind);
    }

    // 1299225426836709378
    @ApiOperation(value="精度测试",tags={},notes="")
    @GetMapping("num")
    public Result num() {
        Long num = 1299225426836709378L;
        return Result.ok().data(num);
    }

    @ApiOperation(value="json",tags={},notes="")
    @GetMapping("json")
    public Result json() {
        HashMap<String, List<String>> map = new HashMap<>();
        Long l = 33L;
        map.put(l.toString(), Arrays.asList(new String[]{"11","22"}));
        return Result.ok().data(map);
    }



    @Autowired
    WordServiceUtil wordService;

    @GetMapping("/exportWord")
    @ResponseBody
    public String createWord(HttpServletRequest request, HttpServletResponse response){
        /** 用于组装word页面需要的数据 */
        Map<String, Object> dataMap = new HashMap<String, Object>();

        /** 组装数据 */
        dataMap.put("title", "文件标题");
        dataMap.put("author", "周伟");
        dataMap.put("content", "freeMark生成Word文档段落内容");


        List<Map<String, Object>> listInfo = new ArrayList<Map<String, Object>>();
        for (int i = 1; i <= 10; i++) {
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("title", "标题" + i);
            map.put("content", "内容" + i);
            map.put("author", "作者" + i);
            listInfo.add(map);
        }
        dataMap.put("listInfo", listInfo);

        /** 文件名称，唯一字符串 */
        Random r = new Random();
        SimpleDateFormat sdf1 = new SimpleDateFormat("yyyyMMdd");
        StringBuffer sb = new StringBuffer();
        sb.append(sdf1.format(new Date()));
        sb.append("_");
        sb.append(r.nextInt(100));

        //文件路径
        String filePath = "E:/test/";

        //文件唯一名称
        String fileOnlyName = "用freemarker生成Word文档_" + sb + ".doc";

        //文件名称
        String fileName = "用freemarker生成Word文档.doc";
//        wordUtil.createWord(dataMap, "template-1.ftl", filePath, fileOnlyName);
        wordService.exportWord(request,response,fileOnlyName,"template-1.ftl",dataMap);
//        String word = WordAction.createWord();
        /** 用于组装word页面需要的数据 */

        return "ok";
    }

}
