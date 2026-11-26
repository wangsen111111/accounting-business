package com.ywjs.jcsy.controller;


import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywjs.jcsy.common.annotation.RequestSingleParam;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.File;
import com.ywjs.jcsy.entity.FileNote;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.service.CollectionService;
import com.ywjs.jcsy.service.FileService;
import com.ywjs.jcsy.service.TagService;
import com.ywjs.jcsy.vo.FileParamVo;
import com.ywjs.jcsy.vo.FilesVo;
import com.ywjs.jcsy.vo.PageVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
@RequestMapping("/file")
@Api(value = "文件控制器", tags = {"文件管理接口"})
public class FileController {

    @Autowired
    FileService fileService;

    @Autowired
    TagService tagService;

    @Autowired
    CollectionService collectionService;

    @PostMapping("update")
    public Result update(@RequestBody File file) {
        file.updateById();
        return Result.ok();
    }

    @GetMapping("/getFileTypes")
    public Result getFileTypes() {
        List<String> fileTypes = fileService.getFileTypes();
        return Result.ok().data(fileTypes);
    }

    @GetMapping("{fileId}/getFileNameById")
    public Result getFileNameById(@PathVariable("fileId") Long fileId) {
        File byId = fileService.getById(fileId);
        String fileName = byId.getFileName();
        if(StrUtil.isEmpty(fileName)){
            fileName = new java.io.File(byId.getFilePath()).getName();
        }
        return Result.ok().data(fileName);
    }


    @ApiOperation(value = "分页获取文件信息", tags = {}, notes = "请求参数：页码、条数、项目id")
    @PostMapping("listPreview/{pageNo}/{pageSize}")
    public Result listByProjectId(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize, @RequestBody Map<String, Object> parmMap) {
        Integer projectId = (Integer) parmMap.get("projectId");
        String fileName = (String) parmMap.get("fileName");
        PageVo<com.ywjs.jcsy.entity.File> vo = fileService.getPageFileByProjectId(pageNo, pageSize, projectId, fileName);
        return Result.ok().data(vo);
    }

    @ApiOperation(value = "根据文件id获取文件", tags = {}, notes = "请求参数：文件id集合")
    @PostMapping("list/fileIds")
    public Result listByFileIds(@RequestSingleParam("fileIds") List<String> fileIds) {
        List<Long> ids = fileIds.stream().map(fileId -> Long.valueOf(fileId)).collect(Collectors.toList());
        List<File> files = new File().selectList(new LambdaQueryWrapper<File>().in(File::getFileId, ids));
        // 转化为FilesVo
        List<FilesVo> vos = files.stream().map(item -> {
            FilesVo filesVo = new FilesVo();
            BeanUtil.copyProperties(item, filesVo);
            // 获取文件标签
            List<String> tags = tagService.getTagsByFileId(filesVo.getFileId());
            filesVo.setTags(tags);
            filesVo.setKey(filesVo.getFileId().toString());
            return filesVo;
        }).collect(Collectors.toList());
        // 获取标签
        return Result.ok().data(vos);
    }

    @ApiOperation(value = "获取文件信息", tags = {}, notes = "请求参数：页码、条数")
    @PostMapping("list/{pageNo}/{pageSize}")
    public Result listPage(@PathVariable("pageNo") Integer pageNo, @PathVariable("pageSize") Integer pageSize, @RequestBody FileParamVo parmMap) {
        PageVo<FilesVo> vo = fileService.getFileDetail(pageNo, pageSize, parmMap);
        return Result.ok().data(vo);
    }

    @ApiOperation(value = "获取文件信息", tags = {}, notes = "请求参数：页码、条数")
    @PostMapping("getFileByParam")
    public Result getFileByParam(@RequestBody FileParamVo parmMap) {
        List<FilesVo> files = fileService.getFileByParam(parmMap);
        return Result.ok().data(files);
    }


    @ApiOperation(value = "根据文件ID删除文件和关联关系", tags = {}, notes = "请求参数：文件id")
    @PostMapping("deleteFileByIds")
    public Result deleteFileByIds(@RequestSingleParam("ids") List<String> ids) {
        List<Long> collect = ids.stream().map(id -> Long.valueOf(id)).collect(Collectors.toList());
        fileService.deleteFileByIds(collect);
        return Result.ok();
    }

    @ApiOperation(value = "文件下载", tags = {}, notes = "根据文件id集下载指定文件")
    @PostMapping("downloadFileByIds")
    public void downloadFileByIds(HttpServletResponse response, @RequestBody Map<String, List<Long>> idsMap) {
        List<Long> ids = idsMap.get("ids");
//        if (ids.size() == 1) {
//            fileService.downLoad(response, ids, null);
//        } else {
//            fileService.downLoad(response, ids, System.currentTimeMillis() + ".zip");
//        }
        fileService.downLoad(response, ids, System.currentTimeMillis() + ".zip");
    }

    @ApiOperation(value = "思维导图下载", tags = {}, notes = "根据思维导图下载指定文件")
    @PostMapping("downloadByMindMap")
    public void downloadByMindMap(HttpServletResponse response, @RequestBody Jsmind jsmind) {
        fileService.downloadByMindMap(response, jsmind);
    }


    @ApiOperation(value = "文件上传", tags = {}, notes = "文件上传")
    @PostMapping("upload")
    public Result fileUpload(@RequestParam("files") MultipartFile[] files, Long projectId) {
        Map<String, List<com.ywjs.jcsy.entity.File>> result;
        if (projectId != null) {
            // 文件上传到指定项目
            result = fileService.fileUpload(files, projectId);
        } else {
            // 思维导图文件上传挂载
            result = fileService.fileUpload(files);
        }
        // 思维导图页文件上传挂载
        return Result.ok().data(result);
    }

    @ApiOperation(value = "文件外链", tags = {}, notes = "文件外链")
    @PostMapping("fileChain/{projectId}")
    public Result fileChain(@RequestSingleParam("paths") String paths, @PathVariable("projectId") Long projectId) {
        List<com.ywjs.jcsy.entity.File> files = fileService.filChain(paths, projectId);
        return Result.ok().data(files);
    }

    @ApiOperation(value = "文件外链挂载", tags = {}, notes = "文件外链")
    @PostMapping("fileChain/mount")
    public Result fileChainMount(@RequestBody Map<String, String> map) {
        String paths = map.get("paths");
        List<com.ywjs.jcsy.entity.File> files = fileService.filChain(paths, null);
        return Result.ok().data(files);
    }

    @ApiOperation(value = "根据文件id获取笔记", tags = {}, notes = "请求参数：文件id")
    @GetMapping("note/{fileId}")
    public Result getFileNoteByFileId(@PathVariable("fileId") Long fileId) {
        FileNote fileNote = new FileNote().selectOne(new LambdaQueryWrapper<FileNote>().eq(FileNote::getFileId, fileId));
        return Result.ok().data(fileNote);
    }

    @ApiOperation(value = "文件标签编辑", tags = {}, notes = "请求参数：标签集合")
    @PostMapping("editFileTag/{fileId}")
    public Result editFileTag(@RequestBody List<String> tags, @PathVariable("fileId") Long fileId) {
        // 获取所有标签
        fileService.editFileTag(tags, fileId);
        return Result.ok();
    }

    @ApiOperation(value = "文件项目id获取文件明细", tags = {}, notes = "请求参数：项目id，返回数据：FileDetailVo(文件信息、文件标签)")
    @GetMapping("list/detail/{projectId}")
    public Result getFileDetail(@PathVariable("projectId") Long projectId) {
        List<Long> fileIds = fileService.getIdsByProjectId(projectId);
        List<com.ywjs.jcsy.entity.File> files = fileService.getFileDetailedByIds(fileIds);
        return Result.ok().data(files);
    }

    @ApiOperation(value = "文件类型数量统计", tags = {}, notes = "")
    @GetMapping(value = {"countFileType/{projectId}", "countFileType"})
    public Result countFileType(@PathVariable(value = "projectId", required = false) Long projectId) {
        // select f.file_type,count(1) from file f,file_project_relation fp where f.file_id = fp.file_id and fp.project_id = 11 group by file_type
        List<Map<String, Object>> countFileType = fileService.countFileType(projectId);
        return Result.ok().data(countFileType);
    }

    @ApiOperation(value = "根据文件id查询文件信息", tags = {}, notes = "")
    @GetMapping("getFileByFileId/{fileId}")
    public Result getFileByFileId(@PathVariable("fileId") Long fileId) {
        return Result.ok().data(new com.ywjs.jcsy.entity.File().setFileId(fileId).selectById());
    }


}

