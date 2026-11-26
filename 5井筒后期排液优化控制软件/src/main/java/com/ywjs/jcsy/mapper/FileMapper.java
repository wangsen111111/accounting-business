package com.ywjs.jcsy.mapper;

import com.ywjs.jcsy.entity.File;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.ywjs.jcsy.vo.SearchVo;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface FileMapper extends BaseMapper<File> {

    List<File> queryFileByParms(@Param("parms") SearchVo parms);

    List<String> getFilePathByNodeName(@Param("nodeName") String nodeName);

    List<File> getPageFileByProjectId(@Param("pageNum") Integer pageNum,@Param("pageSize") Integer pageSize,
                                      @Param("projectId") Integer projectId,@Param("key") String key);

    Integer getFileCountByProjectId(@Param("projectId") Integer projectId,@Param("key") String key);

    List<Map<String, Object>> countFileType(@Param("projectId") Long projectId);

    Integer tagFileCount(@Param("projectId") Long projectId);
}
