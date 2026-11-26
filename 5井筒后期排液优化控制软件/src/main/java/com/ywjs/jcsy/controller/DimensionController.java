package com.ywjs.jcsy.controller;


import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.Dimension;
import com.ywjs.jcsy.service.DimensionService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/dimension")
@Api(value="",tags={"维度管理接口"})
public class DimensionController {

    @Autowired
    DimensionService dimensionService;

    @ApiOperation(value="维度列表展示",tags={},notes="")
    @GetMapping("list")
    public Result list(){
        return Result.ok().data(dimensionService.list());
    }

    // restful 风格请求
    @ApiOperation(value="根据id维度",tags={},notes="")
    @DeleteMapping("delete/{tagId}")
    public Result deleteTagById(@PathVariable Long tagId){
        dimensionService.removeById(tagId);
        return Result.ok();
    }

    @ApiOperation(value="根据id修改标签",tags={},notes="")
    @PutMapping("updateTagById")
    public Result updateTagById(@RequestBody Dimension dimension){
        dimensionService.updateById(dimension);
        return Result.ok();
    }

    @ApiOperation(value="添加维度",tags={},notes="")
    @PostMapping("insert")
    public Result insertTag(@RequestBody Dimension dimension){
        dimensionService.save(dimension);
        return Result.ok();
    }
}

