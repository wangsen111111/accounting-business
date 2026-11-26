package com.ywjs.jcsy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.NodeAttrIndex;
import com.ywjs.jcsy.service.NodeAttrIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
@RequestMapping("/nodeAttrIndex")
public class NodeAttrIndexController {

    @Autowired
    NodeAttrIndexService nodeAttrIndexService;

    @PostMapping("save")
    public Result save(@RequestBody NodeAttrIndex nodeAttrIndex){
        nodeAttrIndexService.save(nodeAttrIndex);
        return Result.ok();
    }

    @PostMapping("update")
    public Result update(@RequestBody NodeAttrIndex nodeAttrIndex){
        nodeAttrIndexService.update(nodeAttrIndex,new LambdaQueryWrapper<NodeAttrIndex>()
                .eq(NodeAttrIndex::getJsmindId,nodeAttrIndex.getJsmindId())
                .eq(NodeAttrIndex::getNodeId,nodeAttrIndex.getNodeId()));
        return Result.ok();
    }

    @PostMapping("remove")
    public Result remove(@RequestBody NodeAttrIndex nodeAttrIndex){
        nodeAttrIndexService.remove(new LambdaQueryWrapper<NodeAttrIndex>()
                .eq(NodeAttrIndex::getJsmindId,nodeAttrIndex.getJsmindId())
                .eq(NodeAttrIndex::getNodeId,nodeAttrIndex.getNodeId()));
        return Result.ok();
    }

}

