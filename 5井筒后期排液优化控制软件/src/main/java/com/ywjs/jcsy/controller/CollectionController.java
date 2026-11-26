package com.ywjs.jcsy.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.CollectionSc;
import com.ywjs.jcsy.factory.EntityFactory;
import com.ywjs.jcsy.service.CollectionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-08-13
 */
@CrossOrigin
@RestController
@RequestMapping("/collection")
@Api(value="",tags={"收藏管理接口"})
public class CollectionController {

    @Autowired
    CollectionService collectionService;

    @ApiOperation(value = "根据文件id批量收藏", tags = {}, notes = "请求参数：文件id集合")
    @PostMapping("insert")
    public Result saveBatch(@RequestBody Map<String,List<CollectionSc>> collectionScs)  {
        // 过滤未收藏 选项
        List<CollectionSc> collectionScsList = collectionScs.get("collectionScs");
        List<String> targetIds = collectionScsList.stream().map(CollectionSc::getTargetId).collect(Collectors.toList());
        // 查收
        List<CollectionSc> dbConllections = collectionService.list(new LambdaQueryWrapper<CollectionSc>().in(CollectionSc::getTargetId, targetIds));
        // 过滤if(){}
        if(!CollectionUtil.isEmpty(dbConllections)){
            List<String> dbTargetIds = dbConllections.stream().map(CollectionSc::getTargetId).collect(Collectors.toList());
            collectionScsList = collectionScsList.stream().filter(item->!dbTargetIds.contains(item.getTargetId())).collect(Collectors.toList());
        }
        if(!CollectionUtil.isEmpty(collectionScsList)){
            collectionService.saveBatch(collectionScsList);
            return Result.ok().message("收藏成功");
        }else {
            return Result.ok().message("选中文件都已收藏");
        }
    }


    @ApiOperation(value = "根据文件id收藏", tags = {}, notes = "请求参数：文件id集合")
    @PostMapping("test")
    public Result collectionFileByIds(@RequestBody CollectionSc collection) {
        collection.insert();
        return Result.ok();
    }

    @ApiOperation(value = "查询所有", tags = {}, notes = "请求参数：文件id集合")
    @GetMapping("list")
    public Result list() {
        return Result.ok().data(EntityFactory.create(CollectionSc.class).selectAll());
    }

}

