package com.ywjs.jcsy.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.ywjs.jcsy.entity.LoggingData;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-12-17
 */
public interface LoggingDataService extends IService<LoggingData> {

    /**
     * 得到文件名
     * @return
     */
    List<LoggingData> getFileNames(Integer projectId);

    /**
     * 得到测井数据
     * @param fileName
     * @return
     */
    List<LoggingData> getLoggingData(String fileName);

    /**
     * 解析文件
     * @param files
     * @param projectId
     * @return List<LoggingData>
     */
    List<LoggingData> fileAnalysis(MultipartFile[] files, Long projectId);

}
