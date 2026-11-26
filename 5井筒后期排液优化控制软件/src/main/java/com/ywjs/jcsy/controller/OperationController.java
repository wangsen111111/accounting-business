package com.ywjs.jcsy.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.Operation;
import com.ywjs.jcsy.vo.OperationVo;
import com.ywjs.jcsy.vo.PageVo;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
@RequestMapping("/operation")
public class OperationController {

    @ApiOperation(value = "最近操作", tags = {}, notes = "")
    @PostMapping("list/{pageNo}/{pageSize}")
    public Result list(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize, @RequestBody Map<String, Object> paramMap) {
        LambdaQueryWrapper<Operation> wrapper = new LambdaQueryWrapper<>();
        String targetName = (String) paramMap.get("targetName");
        if (!StrUtil.isEmpty(targetName)) {
            wrapper.like(Operation::getTargetName, targetName);
        }
        String type = (String) paramMap.get("type");
        if (!StrUtil.isEmpty(type)) {
            wrapper.eq(Operation::getOperationTable, type);
        }
        String createUser = (String) paramMap.get("createUser");
        if (!StrUtil.isEmpty(createUser)) {
            wrapper.eq(Operation::getCreateUser, createUser);
        }
        List<Date> startEndTime = (List<Date>) paramMap.get("startEndTime");
        if (!CollectionUtil.isEmpty(startEndTime)) {
            wrapper.le(Operation::getOperationDate, startEndTime.get(1)).ge(Operation::getOperationDate, startEndTime.get(0));
        }
        IPage<Operation> OperationIPage = new Operation().selectPage(new Page<>(pageNo, pageSize), wrapper.orderByDesc(Operation::getOperationDate));
        Long total = OperationIPage.getTotal();
        List<Operation> records = OperationIPage.getRecords();
        List<OperationVo> collect = records.stream().map(item -> {
            OperationVo operationVo = new OperationVo();
            BeanUtil.copyProperties(item, operationVo);
            operationVo.setKey(operationVo.getOperationId());
            return operationVo;
        }).collect(Collectors.toList());
        PageVo<OperationVo> vo = new PageVo<OperationVo>().setData(collect).setTotal(total.intValue()).setPageNum(pageNo).setPageSize(pageSize);
        return Result.ok().data(vo);
    }
}

