package com.ywjs.jcsy.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.ywjs.jcsy.entity.FileProjectRelation;
import com.ywjs.jcsy.mapper.FileProjectRelationMapper;
import com.ywjs.jcsy.service.FileProjectRelationService;
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
public class FileProjectRelationServiceImpl extends ServiceImpl<FileProjectRelationMapper, FileProjectRelation> implements FileProjectRelationService {

    @Override
    public void removeByFileId(Long fileId) {
        this.remove(new LambdaUpdateWrapper<FileProjectRelation>().eq(FileProjectRelation::getFileId, fileId));
    }
}
