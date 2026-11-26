package com.ywjs.jcsy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ywjs.jcsy.common.annotation.RequestSingleParam;
import com.ywjs.jcsy.common.enums.OperationEnum;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.entity.Operation;
import com.ywjs.jcsy.entity.Project;
import com.ywjs.jcsy.entity.ProjectDimensionRelation;
import com.ywjs.jcsy.factory.EntityFactory;
import com.ywjs.jcsy.service.*;
import com.ywjs.jcsy.vo.PageVo;
import com.ywjs.jcsy.vo.ProjectBaseInfoVo;
import com.ywjs.jcsy.vo.ProjectParamVo;
import com.ywjs.jcsy.vo.ProjectVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * <p>
 * 前端控制器
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
@CrossOrigin
@RestController
@RequestMapping("/project")
@Api(value = "项目控制器", tags = {"项目管理接口"})
public class ProjectController {

    @Autowired
    FileProjectRelationService fileProjectRelationService;
    @Autowired
    ProjectService projectService;

    @Autowired
    DimensionService dimensionService;

    @Autowired
    FileService fileService;

    @Autowired
    ProjectDimensionRelationService projectDimensionRelationService;

    @ApiOperation(value = "新建简易项目", tags = {}, notes = "请求参数：Project")
    @PostMapping("addSimple")
    @Transactional(rollbackFor = Exception.class)
    public Result addProject(@RequestBody Project project){
        project.insert();
        new Operation().setTargetId(project.getProjectId().toString()).setTargetName(project.getProjectName()).setOperationTable(OperationEnum.PROJECT.getValue()).setOperationDescription("新建项目").insert();
        return Result.ok().data(project);
    }

    @ApiOperation(value = "新建项目并绑定脑图", tags = {}, notes = "请求参数：Project")
    @PostMapping("createProjectWithJsmind")
    @Transactional(rollbackFor = Exception.class)
    public Result createProjectWithJsmind(@RequestBody Project project,@RequestParam(value = "jsmindId",required=false) String jsmindId){
        // 检测项目是否存在
        Project dbProject = project.selectOne(Wrappers.<Project>lambdaQuery().eq(Project::getProjectName, project.getProjectName()));
        if(dbProject == null){
            project.insert();
            new Operation().setTargetId(project.getProjectId().toString()).setTargetName(project.getProjectName()).setOperationTable(OperationEnum.PROJECT.getValue()).setOperationDescription("新建项目").insert();
            Jsmind jsmind = EntityFactory.create(Jsmind.class).setId(jsmindId).selectById();
            // 判断当前思维导图关联项目是否需要修改
            if(jsmind.getProjectId()==null){
                jsmind.setProjectId(project.getProjectId()).updateById();
            }
            return Result.ok().data(project);
        }else {
            return Result.error().data("项目已存在，请重新输入项目名称");
        }
    }


    @ApiOperation(value = "获取项目列表及文件数", tags = {}, notes = "")
    @GetMapping("getProjectsAndFileCount")
    public Result getProjectsAndFileCount() {
        List<Map<String,Object>> vos =  projectService.getProjectsAndFileCount();
        return Result.ok().data(vos);
    }


    @ApiOperation(value = "获取项目名称", tags = {}, notes = "")
    @GetMapping("getProjectNames")
    public Result getProjectNames() {
        return Result.ok().data(projectService.getProjectNames());
    }

    @ApiOperation(value = "项目列表展示", tags = {}, notes = "")
    @GetMapping("list")
    public Result list() {
        return Result.ok().data(projectService.list());
    }

    @Transactional(rollbackFor = Exception.class)
    @ApiOperation(value = "根据id修改项目名", tags = {}, notes = "")
    @PostMapping("update")
    public Result updateProjectNameById(@RequestBody Project project) {
        projectService.updateById(project);
        // 操作记录
        Operation operation = new Operation().setTargetId(project.getProjectId().toString()).setTargetName(project.getProjectName())
                .setOperationDate(new Date()).setOperationTable(OperationEnum.PROJECT.getValue()).setOperationDescription("修改项目");
        LambdaQueryWrapper<Operation> wrapper = new LambdaQueryWrapper<Operation>().eq(Operation::getTargetId, project.getProjectId()).eq(Operation::getOperationTable, OperationEnum.PROJECT.getValue());
        if(operation.selectOne(wrapper) != null){
            operation.update(wrapper);
        }else {
            operation.insert();
        }
        return Result.ok();
    }

    @ApiOperation(value = "根据项目id绑定该项目维度", tags = {}, notes = "")
    @PostMapping("bind/dimension/{projectId}/{dimensionId}")
    public Result bindDimensionById(@PathVariable Long projectId, @PathVariable Long dimensionId) {
        ProjectDimensionRelation projectDimensionRelation = new ProjectDimensionRelation()
                .setProjectId(projectId)
                .setDimensionId(dimensionId);
        projectDimensionRelationService.save(projectDimensionRelation);
        return Result.ok();
    }


    @ApiOperation(value = "批量项目导入(本地模式)", tags = {}, notes = "")
    @PostMapping("import/chain/{saveType}")
    public Result importChain(@RequestSingleParam("projectPaths") String projectPaths,@PathVariable("saveType") Integer saveType) {
        String[] paths = projectPaths.split("\n");
        List<String> list = Arrays.asList(paths);
        return Result.ok().data(projectService.importChain(list,saveType));
    }

    @ApiOperation(value = "批量项目导入(服务模式)", tags = {}, notes = "")
    @PostMapping("import/upload/{saveType}")
    public Result importUpload(@RequestParam("files") MultipartFile[] files,@PathVariable("saveType") Integer saveType) throws IOException {
        return Result.ok().data(projectService.importProjects(files,saveType));
    }

    @ApiOperation(value = "分页获取项目", tags = {}, notes = "请求参数：页码、条数")
    @PostMapping("listPage/{pageNo}/{pageSize}")
    public Result getProjectsByPage(@PathVariable("pageNo") Long pageNo, @PathVariable("pageSize") Long pageSize,@RequestBody ProjectParamVo vo) {
        PageVo<ProjectVo> pageVo = projectService.listPage(pageNo,pageSize,vo);
        return Result.ok().data(pageVo);
    }

    @ApiOperation(value = "根据id删除项目及其关联信息", tags = {}, notes = "")
    @PostMapping("deleteProjectByIds")
    public Result deleteProjectByIds(@RequestSingleParam("ids") List<Integer> ids) {
        projectService.deleteProjectByIds(ids);
        return Result.ok();
    }

    @ApiOperation(value = "根据项目id导出项目", tags = {}, notes = "思路：若该项目为外链形式构成，创建临时目录以思维导图结构将文件组织为一个结构化目录，最后将该目录打包成压缩文件以供下载，最后删除临时文件；否则直接打包下载")
    @PostMapping("downloadByIds/{projectId}")
    public void outputProject(HttpServletResponse response,@PathVariable("projectId") Long projectId) {
        projectService.outputProject(projectId,response);
    }

    @ApiOperation(value = "获取项目基本信息", tags = {}, notes = "请求参数：projectId")
    @GetMapping("getProjectBaseInfo/{projectId}")
    public Result getProjectBaseInfo(@PathVariable("projectId") Long projectId) {
        ProjectBaseInfoVo vo= projectService.getProjectBaseInfo(projectId);
        return Result.ok().data(vo);
    }

    @ApiOperation(value = "根据项目id获取项目信息", tags = {}, notes = "请求参数：projectId")
    @GetMapping("getProjectByid/{projectId}")
    public Result getProjectByid(@PathVariable("projectId") Long projectId) {
        Project project = new Project().setProjectId(projectId).selectById();
        return Result.ok().data(project);
    }

}

