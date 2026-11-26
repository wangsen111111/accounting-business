package com.ywjs.jcsy.controller;

import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.File;
import com.ywjs.jcsy.service.FileService;
import com.ywjs.jcsy.vo.SearchVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author zhouwei
 * @create 2020-07-30 17:13
 */
@CrossOrigin
@RestController
@RequestMapping("/search")
@Api(value="检索控制器",tags={"检索服务接口"})
public class SearchController {

    @Autowired
    FileService fileService;

    // 检索条件：文件名、文件类型、标签、项目名、维度、思维导图节点名，扩展根据文件笔记检索（全文检索技术）
    // 展现形式：文件列表形式、思维导图形式

    /*
    查询某个维度某个项目节点下的文件
    select f.file_id,f.file_name,f.file_path,f.file_type from file_project_relation fp
    inner join project p  on  fp.project_id = p.project_id
    inner join mind_map mm on p.project_id = mm.project_id
    inner join file f on f.file_id = fp.file_id
    inner join file_node_relation fn on fn.file_id = f.file_id
    inner join project_dimension_relation pd on pd.project_id = pd.project_id
    inner join dimension d on pd.dimension_id = d.dimension_id
    inner join file_tag_relation fr on fr.file_id = f.file_id
    inner join tag t on t.tag_id = fr.tag_id
    where p.project_name = '项目1' and mm.node_name = '节点1'
     and d.dimension_name = '维度1' and t.tag_name = '论文'
     */
    @ApiOperation(value="根据条件检索文件列表",tags={},notes="请求参数：文件名、文件类型、标签、项目名、维度、思维导图节点名")
    @PostMapping("list")
    public Result searchList(@RequestBody SearchVo searchVo){
        List<File> files = fileService.queryFileByParms(searchVo);
        return Result.ok().data(files);
    }


    @ApiOperation(value="根据条件检索文件树",tags={},notes="请求参数：文件名、文件类型、标签、项目名、维度、思维导图节点id")
    @PostMapping("tree")
    public Result searchTree(){
        return null;
    }



}
