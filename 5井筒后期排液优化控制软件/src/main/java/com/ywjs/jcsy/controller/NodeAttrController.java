package com.ywjs.jcsy.controller;


import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.NodeAttr;
import com.ywjs.jcsy.service.NodeAttrService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-10-27
 */
@CrossOrigin
@RestController
@RequestMapping("/nodeAttr")
public class NodeAttrController {

    @Autowired
    NodeAttrService nodeAttrService;
    // 获取属性数据
    @GetMapping("getNodeAttr")
    public Result getNodeAttr(){
        List<NodeAttr> list = nodeAttrService.list();
        Map<String, List<NodeAttr>> collect = list.stream().collect(Collectors.groupingBy(NodeAttr::getNodeAttr));
        return Result.ok().data(collect);
    }



}

