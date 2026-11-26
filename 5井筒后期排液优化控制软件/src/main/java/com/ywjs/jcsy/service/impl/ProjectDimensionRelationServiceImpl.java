package com.ywjs.jcsy.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ywjs.jcsy.entity.ProjectDimensionRelation;
import com.ywjs.jcsy.mapper.ProjectDimensionRelationMapper;
import com.ywjs.jcsy.service.ProjectDimensionRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
@Service
@Transactional
public class ProjectDimensionRelationServiceImpl extends ServiceImpl<ProjectDimensionRelationMapper, ProjectDimensionRelation> implements ProjectDimensionRelationService {

    @Override
    public void removeBatchByProjectId(Long projectId) {
        this.remove(new LambdaUpdateWrapper<ProjectDimensionRelation>().eq(ProjectDimensionRelation::getProjectId, projectId));
    }
}
