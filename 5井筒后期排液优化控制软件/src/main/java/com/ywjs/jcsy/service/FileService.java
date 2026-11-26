package com.ywjs.jcsy.service;

import com.ywjs.jcsy.entity.File;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.vo.FileParamVo;
import com.ywjs.jcsy.vo.FilesVo;
import com.ywjs.jcsy.vo.PageVo;
import com.ywjs.jcsy.vo.SearchVo;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface FileService extends IService<File> {

    void downLoad(HttpServletResponse response,List<Long> fileIds,String fileName);

    List<File> queryFileByParms(SearchVo searchVo);

    List<String> getFilePathByNodeName(String nodeName);

    /**
     * 通过项目ID获取文件ID
     * @param projectId
     * @return List<Long>
     */
    List<Long> getIdsByProjectId(Long projectId);

    /**
     * 通过文件Id数组 获取文件详细信息
     * @param fileIds
     * @return List<File>
     */
    List<File> getFileDetailedByIds(List<Long> fileIds);

    PageVo<FilesVo> getFileDetail(Integer pageNum, Integer pageCount, FileParamVo parmMap);

    void deleteFileByIds(List<Long> ids);

    void editFileTag(List<String> tags,Long fileId);

    PageVo<File> getPageFileByProjectId(Integer pageNum, Integer pageSize, Integer projectId,String fileName);

    Map<String, List<File>> fileUpload(MultipartFile[] files, Long projectId);

    List<File> filChain(String pathString,Long projectId);

    List<Map<String, Object>> countFileType(Long projectId);

    Integer tagFileCount(Long projectId);

    Map<String, List<File>> fileUpload(MultipartFile[] files);

    /**
     * 根据思维导图传输条件下载文件
     * @param response
     * @param jsmind
     */
    void downloadByMindMap(HttpServletResponse response, Jsmind jsmind);

    /**
     * 获取文件类型
     * @return
     */
    List<String> getFileTypes();

    List<FilesVo> getFileByParam(FileParamVo parmMap);
}
