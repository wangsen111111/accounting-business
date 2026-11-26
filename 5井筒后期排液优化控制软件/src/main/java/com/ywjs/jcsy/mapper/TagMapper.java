package com.ywjs.jcsy.mapper;

import com.ywjs.jcsy.entity.Tag;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
public interface TagMapper extends BaseMapper<Tag> {

    List<Map<String, Object>> getHotTagWithFileCount(@Param("projectId") Long projectId);

    List<String> getTagsByFileId(Long fileId);
}
