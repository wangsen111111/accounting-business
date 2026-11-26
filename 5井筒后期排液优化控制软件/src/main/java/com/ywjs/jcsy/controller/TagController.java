package com.ywjs.jcsy.controller;


import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.Tag;
import com.ywjs.jcsy.service.TagService;
import com.ywjs.jcsy.vo.TagScreeningVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
@CrossOrigin
@RestController
@RequestMapping("/tag")
@Api(value="标签控制器",tags={"标签管理接口"})
public class TagController {

    @Autowired
    TagService tagService;

    @ApiOperation(value="标签列表展示",tags={},notes="")
    @GetMapping("list")
    public Result list(){
        return Result.ok().data(tagService.list());
    }

    // restful 风格请求
    @ApiOperation(value="根据id删除标签",tags={},notes="")
    @GetMapping("delete/{tagId}")
    public Result deleteTagById(@PathVariable  Long tagId){
        tagService.deleteTagById(tagId);
        return Result.ok();
    }

    @ApiOperation(value="根据id修改标签",tags={},notes="")
    @PostMapping("updateTagById")
    public Result updateTagById(@RequestBody Tag tag){
        tagService.updateById(tag);
        return Result.ok();
    }

    @ApiOperation(value="添加标签",tags={},notes="")
    @PostMapping("insert")
    public Result insertTag(@RequestBody Tag tag){
        tagService.save(tag);
        return Result.ok().data(tag);
    }

    @ApiOperation(value="获取标签及该标签下的文件数",tags={},notes="")
    @GetMapping("getHotTagWithFileCount")
    public Result getHotTagWithFileCount(@RequestParam(required = false) Long projectId){
        List<Map<String,Object>> data = tagService.getHotTagWithFileCount(projectId);
        return Result.ok().data(data);
    }

    /**
     * 获取标签检索页面数据
     * @return
     */
    @GetMapping("tagScreening")
    public Result tagScreening(){
        TagScreeningVo vo = tagService.tagScreening();
        return Result.ok().data(vo);
    }

    @PostMapping("/multipleTagMount")
    public Result multipleTagMount(@RequestBody Map<String,List<String>> parmMap){
        List<Map<String, Object>> tagNames = tagService.multipleTagMount(parmMap.get("tagNames"));
        return Result.ok().data(tagNames);
    }

    @PostMapping("/singleTagMount")
    public Result singleTagMount(@RequestParam("model") Integer model, @RequestBody Map<String,List<String>> parmMap){
        List<String> result = tagService.singleTagMount(model,parmMap.get("tagNames"));
        return Result.ok().data(result);
    }

}

