package com.ywjs.jcsy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.service.JsmindService;
import com.ywjs.jcsy.utils.JsmindUtil;
import com.ywjs.jcsy.vo.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-08-21
 */
@CrossOrigin
@RestController
@RequestMapping("/jsmind")
@Api(value = "", tags = {"jsmind思维导图管理接口"})
public class JsmindController {

    @Autowired
    JsmindService jsmindService;

    @ApiOperation(value = "根据项目id查询思维导图", tags = {}, notes = "")
    @GetMapping("list/{projectId}")
    public Result getMindMapByProjectId(@PathVariable Long projectId) {
        List<Jsmind> jsminds = new Jsmind().selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getProjectId, projectId).orderByDesc(Jsmind::getCreateTime));
        return Result.ok().data(jsminds);
    }

    @ApiOperation(value = "根据脑图id查询思维导图", tags = {}, notes = "")
    @GetMapping("getMindMapById/{jsmindId}")
    public Result list(@PathVariable String jsmindId) {
        Jsmind jsmind = new Jsmind().setId(jsmindId).selectById();
        return Result.ok().data(jsmind);
    }

    @ApiOperation(value = "查询全部思维导图", tags = {}, notes = "")
    @GetMapping("list")
    public Result getList() {
        List<Jsmind> jsMinds = new Jsmind().selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getIsTemplate,0).orderByDesc(Jsmind::getUpdateTime));
        return Result.ok().data(jsMinds);
    }

    @ApiOperation(value = "根据维度id查询思维导图", tags = {}, notes = "")
    @GetMapping("getMindMapByDimensionId/{dimensionId}/{projectId}")
    public Result getMindMapByDimensionId(@PathVariable Long dimensionId,@PathVariable Long projectId) {
        List<Jsmind> list = jsmindService.list(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getDimensionId, dimensionId).ne(Jsmind::getProjectId,projectId));
        list.forEach(item->JsmindUtil.unMountAndChangeTopic(item.getData().getData()));
        return Result.ok().data(list);
    }

    @ApiOperation(value = "根据id删除思维导图", tags = {}, notes = "")
    @GetMapping("remove/{id}")
    public Result remove(@PathVariable String id) {
        jsmindService.deleteById(id);
        return Result.ok();
    }

    @ApiOperation(value = "校验思维导图名称是否存在", tags = {}, notes = "")
    @GetMapping("checkMindMapName")
    public Result checkMindMapName(@RequestParam("name") String name) {
        Boolean isExist = jsmindService.checkMindMapName(name);
        if(isExist)  return Result.ok();
        else return Result.error();
    }

    @ApiOperation(value = "最近操作思维导图", tags = {}, notes = "")
    @GetMapping("getOperationJsminds")
    public Result getOperationJsminds() {
//        Page<Operation> page = operationService.page(new Page<>(1, 20), new LambdaQueryWrapper<Operation>().eq(Operation::getOperationTable, "jsmind").orderByDesc());
//        List<Operation> records = page.getRecords();
//        List<Operation> operationJsminds = new Operation().selectList(new LambdaQueryWrapper<Operation>().eq(Operation::getOperationTable, "jsmind").orderByAsc());
//        // 过滤模板
//        List<String> jsmindIds = operationJsminds.stream().map(Operation::getTargetId).collect(Collectors.toList());
//        List<Jsmind> jsminds = jsmindService.getBaseMapper().selectList(new LambdaQueryWrapper<Jsmind>().in(Jsmind::getId, jsmindIds).eq(Jsmind::getIsTemplate, 0).orderByDesc(Jsmind::getUpdateTime));
        // 为避免操作未加入操作表直接取出最近操作20条数据
        IPage<Jsmind> jsmindIPage = new Jsmind().selectPage(new Page<>(1, 10), new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getIsTemplate,false).orderByAsc(Jsmind::getUpdateTime));
        List<Jsmind> jsminds = jsmindIPage.getRecords();
        return Result.ok().data(jsminds);
    }

    @ApiOperation(value = "添加思维导图", tags = {}, notes = "")
    @PostMapping("add")
    public Result add(@RequestBody Jsmind jsmind) {
        return Result.ok().data(jsmindService.add(jsmind));
    }

    @ApiOperation(value = "保存或更新思维导图", tags = {}, notes = "")
    @PostMapping("saveOrUpdateMindMap")
    public Result saveOrUpdateMindMap(@RequestBody Jsmind jsmind) {
        jsmindService.saveOrUpdateMindMap(jsmind);
        return Result.ok();
    }

    @ApiOperation(value = "保存思维导图为模板", tags = {}, notes = "")
    @PostMapping("add/template")
    public Result saveMindMapTotemplate(@RequestBody Jsmind jsmind,@RequestParam("name") String name) {
        jsmindService.saveMindMapTotemplate(jsmind,name);
        return Result.ok();
    }

    @ApiOperation(value = "获取思维导图为模板", tags = {}, notes = "")
    @GetMapping("getTemplate")
    public Result getTemplate() {
        List<Jsmind> jsminds = jsmindService.getBaseMapper().selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getIsTemplate, 1));
        // 改变模板思维导图自定义属性
        jsminds.forEach(jsmind->{
            JsmindDataVo.Attribute atribute = jsmind.getData().getAtribute();
            atribute.setActualZoom(0.5);
        });
        return Result.ok().data(jsminds);
    }

    @ApiOperation(value = "根据文件id和节点id卸载文件", tags = {}, notes = "")
    @GetMapping("uninstallFile")
    public Result uninstallFile(@RequestParam("jsmindId") String jsmindId,@RequestParam("nodeId") String nodeId,@RequestParam("fileId") Long fileId) {
        jsmindService.uninstallFile(jsmindId,nodeId,fileId);
        return Result.ok();
    }


    @ApiOperation(value = "统计节点文件数量", tags = {}, notes = "")
    @GetMapping(value = {"countNodeFile/{projectId}","countNodeFile"})
    public Result countNodeFile(@PathVariable(value = "projectId",required = false) Long projectId) {
        // 查询项目下思维导图
        List<CountVo> countNodeFile = jsmindService.countNodeFile(projectId);
        return Result.ok().data(countNodeFile);
    }

    @ApiOperation(value = "思维导图标签统计", tags = {}, notes = "")
    @GetMapping("countTag/{projectId}")
    public Result countTag(@PathVariable("projectId") Long projectId) {
        Object vo = jsmindService.countTag(projectId);
        return Result.ok().data(vo);
    }



    @GetMapping("getNodesByProjectId")
    public Result getNodesByProjectId(@RequestParam("projectId") Long projectId){
        List<JsmindDataVo.JsMindData> data =  jsmindService.getNodesByProjectId(projectId);
        return Result.ok().data(data);
    }

    @GetMapping("getAssociated")
    public Result getAssociated(@RequestParam(value = "jsmindId" , required = false) String jsmindId){
        AssociatedVo data = jsmindService.getAssociated(jsmindId);
        return Result.ok().data(data);
    }

    /**
     * 思维导图关联维度和项目
     * @param associatedId
     * @return
     */
    @PostMapping("associated")
    public Result associated(@RequestBody AssociatedIdVo associatedId) {
        jsmindService.associated(associatedId);
        return Result.ok();
    }

    /**
     * 拷贝节点
     * @param jsmind
     * @param nodeId
     * @return
     */
    @PostMapping("{nodeId}/getCopyNodeByNodeId")
    public Result getCopyNodeByNodeId(@RequestBody Jsmind jsmind,@PathVariable("nodeId") String nodeId) {
        JsmindDataVo.JsMindData node = jsmindService.getCopyNodeByNodeId(jsmind,nodeId);
        return Result.ok().data(node);
    }

    /**
     * 粘贴节点
     * @param vo
     * @return
     */
    @PostMapping("/pasteNode")
    public Result pasteNode(@RequestBody PasteNodeVo vo) {
        Jsmind jsmind= jsmindService.pasteNode(vo);
        return Result.ok().data(jsmind);
    }

    /**
     * 思维导出
     * @param
     * @return
     */
    @PostMapping("/exportJsmind") // @PathVariable("jsmindId") String jsmindId,  "/{jsmindId}/exportJsmind"
    public void exportJsmind(@RequestBody Jsmind jsmind,HttpServletResponse response) throws Exception {
        jsmindService.exportJsmind(jsmind,response);
    }

    /**
     * 思维导图导入
     * @param files
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "导入思维导图(服务模式)", tags = {}, notes = "")
    @PostMapping("import")
    public Result inputFile(@RequestParam("files") MultipartFile files) {
        Jsmind jsmind = jsmindService.importUpload(files);
        jsmind.insert();
        return Result.ok().data(jsmind);
    }

    /**
     * 思维导图节点导入 @RequestParam String nodeId,
     * @param files
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "导入思维导图(服务模式)", tags = {}, notes = "")
    @PostMapping("importNode")
    public Result inputMindMapNode(@RequestParam("files") MultipartFile files,@RequestPart Jsmind jsmind,@RequestParam String nodeId) {
        Jsmind jsmindResult = jsmindService.importNode(files,nodeId,jsmind);
        return Result.ok().data(jsmindResult);
    }

    @ApiOperation(value = "首页思维导图", tags = {}, notes = "")
    @GetMapping("getJsmindBydimensionId/{dimensionId}")
    public Result getJsmindBydimensionId(@PathVariable("dimensionId") Long dimensionId) {
        Jsmind data = jsmindService.getJsmindBydimensionId(dimensionId);
        return Result.ok().data(data);
    }

    /**
     * @return
     * @throws IOException
     */
    @ApiOperation(value = "自动生成思维导图", tags = {}, notes = "")
    @PostMapping("generateJsmind")
    public Result generateJsmind(@RequestBody List<String> jsmindIds,@RequestParam String dimensionName) {
        Jsmind jsmind = jsmindService.generateJsmind(jsmindIds,dimensionName);
        return Result.ok().data(jsmind);
    }
}
