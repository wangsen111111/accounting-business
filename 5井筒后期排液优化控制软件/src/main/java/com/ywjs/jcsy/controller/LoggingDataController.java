package com.ywjs.jcsy.controller;

import com.ywjs.jcsy.common.result.Result;
import com.ywjs.jcsy.entity.LoggingData;
import com.ywjs.jcsy.service.LoggingDataService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * @author
 */
@CrossOrigin
@RestController
@RequestMapping("/loggingData")
@Api(value = "解析文件", tags = {"解析文件接口"})
public class LoggingDataController {

    @Autowired
    LoggingDataService loggingDataService;

    @ApiOperation(value = "解析文件", tags = {}, notes = "")
    @PostMapping("fileAnalysis")
    public Result fileAnalysis(@RequestParam("files") MultipartFile[] files, Long projectId) {
        List<LoggingData> loggingDataList = loggingDataService.fileAnalysis(files, projectId);
        return Result.ok().data(loggingDataList);
    }

    @GetMapping("getFileNames/{projectId}")
    public Result getFileNames(@PathVariable("projectId") Integer projectId) {
        List<LoggingData> fileNames = loggingDataService.getFileNames(projectId);
        return Result.ok().data(fileNames);
    }

    @GetMapping("getLoggingData/{fileName}")
    public Result getLoggingData(@PathVariable("fileName") String fileName) {
        List<LoggingData> fileNames = loggingDataService.getLoggingData(fileName);
        return Result.ok().data(fileNames);
    }
}
