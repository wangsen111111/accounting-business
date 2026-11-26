package com.ywjs.jcsy.service;

import com.ywjs.jcsy.entity.FileProjectRelation;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface FileProjectRelationService extends IService<FileProjectRelation> {
    /**
     * 根据文件ID 删除文件项目关联关系
     * @param fileId
     */
    void removeByFileId(Long fileId);
}
