package com.ywjs.jcsy.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.Wrapper;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.ywjs.jcsy.entity.FileTagRelation;
import com.ywjs.jcsy.entity.Tag;
import com.ywjs.jcsy.mapper.FileTagRelationMapper;
import com.ywjs.jcsy.service.FileTagRelationService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywjs.jcsy.service.TagService;
import com.ywjs.jcsy.vo.BatchFileTagVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

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
public class FileTagRelationServiceImpl extends ServiceImpl<FileTagRelationMapper, FileTagRelation> implements FileTagRelationService {

    @Autowired
    TagService tagService;

    @Override
    public List<FileTagRelation> getListByFileIds(List<Long> fileIds) {
        List<FileTagRelation> fileTagRelations = this.list(new LambdaQueryWrapper<FileTagRelation>().in(FileTagRelation::getFileId, fileIds));
        return fileTagRelations;
    }

    @Override
    public void removeByFileId(Long fileId) {
        this.remove(new LambdaUpdateWrapper<FileTagRelation>().eq(FileTagRelation::getFileId, fileId));
    }

    @Override
    public void batchFileTag(BatchFileTagVo vo) {
        List<Long> fileIds = vo.getFileIds();
        List<String> tagNames = vo.getTagNames();

        // 查看是否有新需要添加的标签
        List<Tag> dbTags = tagService.list(Wrappers.<Tag>lambdaQuery().in(Tag::getTagName, tagNames));
        // 过滤需要添加的标签
        if(!CollectionUtil.isEmpty(dbTags)){
            List<String> dbTagNames = dbTags.stream().map(Tag::getTagName).collect(Collectors.toList());
            List<String> insertTagNames = tagNames.stream().filter(tagName -> !dbTagNames.contains(tagName)).collect(Collectors.toList());
            if(!CollectionUtil.isEmpty(insertTagNames)){
                List<Tag> insertTags = insertTagNames.stream().map(tagName -> new Tag().setTagName(tagName)).collect(Collectors.toList());
                tagService.saveBatch(insertTags);
            }
        }

        // 再次查询数据库标签获取最新的标签
        dbTags = tagService.list(Wrappers.<Tag>lambdaQuery().in(Tag::getTagName, tagNames));

        // 查询文件已有标签
        List<FileTagRelation> dbTagsWithFileIds = this.list(Wrappers.<FileTagRelation>lambdaQuery().in(FileTagRelation::getFileId, fileIds));
        for (Long fileId : fileIds) {
            // 过滤需要插图的标签
            List<Tag> insertTags = dbTags.stream().filter(tag -> {
                // 过滤当前文件打的标签
                List<FileTagRelation> currFileTag = dbTagsWithFileIds.stream().filter(item -> item.getFileId().equals(fileId)).collect(Collectors.toList());
                if (!CollectionUtil.isEmpty(currFileTag)) {
                    List<Long> collect = currFileTag.stream().map(FileTagRelation::getTagId).filter(item -> item.equals(tag.getTagId())).collect(Collectors.toList());
                    if (!CollectionUtil.isEmpty(collect))  return false;
                }
                return true;
            }).collect(Collectors.toList());
            List<FileTagRelation> insertFileTagRelation = insertTags.stream().map(tag -> new FileTagRelation().setTagId(tag.getTagId()).setFileId(fileId)).collect(Collectors.toList());
            if(!CollectionUtil.isEmpty(insertFileTagRelation)){
                this.saveBatch(insertFileTagRelation);
            }
        }





    }
}
