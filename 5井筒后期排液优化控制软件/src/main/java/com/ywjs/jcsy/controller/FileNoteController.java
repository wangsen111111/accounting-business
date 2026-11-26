package com.ywjs.jcsy.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.File;
import com.ywjs.jcsy.entity.FileNote;
import com.ywjs.jcsy.service.FileNoteService;
import com.ywjs.jcsy.utils.WordServiceUtil;
import com.ywjs.jcsy.vo.NoteTemplateVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
@RequestMapping("/note")
@Api(value="笔记控制器",tags={"笔记管理接口"})
public class FileNoteController {

    @Autowired
    FileNoteService fileNoteService;
    @Autowired
    WordServiceUtil wordServiceUtil;

    @ApiOperation(value="根据文件id添加笔记",tags={},notes="请求参数：笔记对象（FileNote）")
    @PostMapping("add")
    public Result add(@RequestBody FileNote fileNote){
        fileNote.insert();
        return Result.ok();
    }

    @GetMapping("/removeFileNoteById/{id}")
    public Result removeFileNoteById(@PathVariable("id") Long id){
        fileNoteService.removeById(id);
        return Result.ok();
    }

    @ApiOperation(value="根据文件id保存或修改笔记",tags={},notes="请求参数：笔记对象（FileNote）")
    @PostMapping("update")
    public Result update(@RequestBody FileNote fileNote){
        fileNoteService.saveOrUpdate(fileNote,new LambdaQueryWrapper<FileNote>().eq(FileNote::getId,fileNote.getId()));
        return Result.ok().data(fileNote);
    }

    @ApiOperation(value="根据文件id查询笔记",tags={},notes="请求参数：笔记对象（FileNote）")
    @GetMapping("list/{fileId}")
    public Result getFileNoteByFileId(@PathVariable Long fileId){
        List<FileNote> fileNotes = new FileNote().selectList(new LambdaQueryWrapper<FileNote>().eq(FileNote::getFileId, fileId));
        return Result.ok().data(fileNotes);
    }

    @ApiOperation(value="根据文件id查询笔记",tags={},notes="请求参数：笔记对象（FileNote）")
    @PostMapping("/exportNoteByFile")
    public void exportNoteByFile(@RequestBody File File, HttpServletRequest request, HttpServletResponse response){
        NoteTemplateVo noteTemplateVo = fileNoteService.exportNoteByFileId(File);
        String fileName = noteTemplateVo.getFile().getFileName() + "文件笔记";
        wordServiceUtil.exportWord(request,response,fileName,"template.ftl",noteTemplateVo);
    }



}

