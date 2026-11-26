package com.ywjs.jcsy.controller;


import cn.hutool.core.collection.CollectionUtil;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.FileTagRelation;
import com.ywjs.jcsy.service.FileTagRelationService;
import com.ywjs.jcsy.vo.BatchFileTagVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
@RequestMapping("/file-tag-relation")
public class FileTagRelationController {

    @Autowired
    FileTagRelationService fileTagRelationService;

    @ApiOperation(value="添加文件标签关联关系",tags={},notes="")
    @PostMapping("insert")
    public Result insertFileTagRelations(@RequestBody  List<FileTagRelation> fileTagRelations){
        if(!CollectionUtil.isEmpty(fileTagRelations)){
            fileTagRelationService.saveBatch(fileTagRelations);
        }
        return Result.ok().data(fileTagRelations);
    }



    @ApiOperation(value="添加文件标签关联关系",tags={},notes="")
    @PostMapping("batchFileTags")
    public Result batchFileTags(@RequestBody BatchFileTagVo vo){
        fileTagRelationService.batchFileTag(vo);
        return Result.ok().data("");
    }
}

