package com.ywjs.jcsy.service;

import com.ywjs.jcsy.entity.ProjectDimensionRelation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface ProjectDimensionRelationService extends IService<ProjectDimensionRelation> {

    /**
     * 根据projectId 删除 文件维度关联关系
     * @param projectId
     */
    void removeBatchByProjectId(Long projectId);
}
