package com.ywjs.jcsy.mapper;

import com.ywjs.jcsy.entity.Project;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

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
public interface ProjectMapper extends BaseMapper<Project> {

    List<Map<String,Object>> getProjectsAndFileCount();
}
