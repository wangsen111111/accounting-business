package com.ywjs.jcsy.service;

import com.ywjs.jcsy.entity.FileTagRelation;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywjs.jcsy.vo.BatchFileTagVo;

import java.util.List;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface FileTagRelationService extends IService<FileTagRelation> {
    /**
     * 通过文件ID 获取 文件标签关联关系
     * @param fileIds
     * @return
     */
    List<FileTagRelation> getListByFileIds(List<Long> fileIds);

    /**
     * 根据文件ID删除 文件标签关联关系
     * @param fileId
     */
    void removeByFileId(Long fileId);

    void batchFileTag(BatchFileTagVo vo);
}
