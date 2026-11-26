package com.ywjs.jcsy.service.impl;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywjs.jcsy.entity.FileTagRelation;
import com.ywjs.jcsy.entity.Tag;
import com.ywjs.jcsy.factory.EntityFactory;
import com.ywjs.jcsy.mapper.FileTagRelationMapper;
import com.ywjs.jcsy.mapper.TagMapper;
import com.ywjs.jcsy.service.FileService;
import com.ywjs.jcsy.service.ProjectService;
import com.ywjs.jcsy.service.TagService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywjs.jcsy.vo.Project3LevelVo;
import com.ywjs.jcsy.vo.TagScreeningVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
public class TagServiceImpl extends ServiceImpl<TagMapper, Tag> implements TagService {

    @Autowired
    FileTagRelationMapper fileTagRelationMapper;
    @Autowired
    ProjectService projectService;
    @Autowired
    FileService fileService;
    @Override
    public void deleteTagById(Long tagId) {
        // 判断便签是否被文件引用
        Integer count = fileTagRelationMapper.selectCount(new LambdaQueryWrapper<FileTagRelation>().eq(FileTagRelation::getTagId, tagId));
        if (count == 0){
            // 无引用，可以删除
            this.removeById(tagId);
        }
    }

    @Override
    public List<Map<String, Object>> getHotTagWithFileCount(Long projectId) {
        return this.baseMapper.getHotTagWithFileCount(projectId);
    }

    @Override
    public List<String> getTagsByFileId(Long fileId) {
        return this.baseMapper.getTagsByFileId(fileId);
    }

    @Override
    public TagScreeningVo tagScreening() {
        TagScreeningVo tagScreeningVo = new TagScreeningVo();
        // 获取所有标签
        List<Tag> tags = this.list();
        tagScreeningVo.setTags(tags);
        // 组织思维导图三级分类
        List<Project3LevelVo> project3Level = projectService.getProject3Level();
        tagScreeningVo.setProjectScreeningVos(project3Level);
        // 文件类型
        List<String> fileTypes = fileService.getFileTypes();
        tagScreeningVo.setFileTypes(fileTypes);
        return tagScreeningVo;
    }

    @Override
    public List<Map<String, Object>> multipleTagMount( List<String> tagNames) {
        List<Map<String, Object>> list = new ArrayList<>();
        for (String tagName : tagNames) {
            // 获取当前标签下的文件
            Tag tag = this.baseMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, tagName));
            List<FileTagRelation> fileTagRelations = EntityFactory.create(FileTagRelation.class).selectList(new LambdaQueryWrapper<FileTagRelation>().eq(FileTagRelation::getTagId, tag.getTagId()));
            // 获取文件id
            List<Long> fileIds = fileTagRelations.stream().map(FileTagRelation::getFileId).collect(Collectors.toList());
            List<String> collect = fileIds.stream().map(item -> item.toString()).collect(Collectors.toList());
            Map<String, Object> map = new HashMap<>();
            map.put("tagName",tagName);
            map.put("fileIds",collect);
            list.add(map);
        }
        return list;
    }

    @Override
    public List<String> singleTagMount(Integer model, List<String> tagNames) {
        List<List<Long>> list = new ArrayList<>();
        for (String tagName : tagNames) {
            // 获取当前标签下的文件
            Tag tag = this.baseMapper.selectOne(new LambdaQueryWrapper<Tag>().eq(Tag::getTagName, tagName));
            List<FileTagRelation> fileTagRelations = new FileTagRelation().selectList(new LambdaQueryWrapper<FileTagRelation>().eq(FileTagRelation::getTagId, tag.getTagId()));
            // 获取文件id
            List<Long> fileIds = fileTagRelations.stream().map(FileTagRelation::getFileId).collect(Collectors.toList());
            list.add(fileIds);
        }

        List<Long> result = new ArrayList<>();
        // 交集
        if(model == 0){
            if(!CollectionUtil.isEmpty(list)){
                // 取出第一个元素作为基数组
                List<Long> baseList = list.get(0);
                for (List<Long> item : list) {
                    baseList = baseList.stream().filter(num -> item.contains(num)).collect(Collectors.toList());
                }
                result = baseList;
            }
        }

        //并集  去重
        if(model == 1){
            if(!CollectionUtil.isEmpty(list)){
                List<Long> andSetList = new ArrayList<>();
                for (List<Long> item : list) {
                    andSetList.addAll(item);
                }
                andSetList = andSetList.stream().distinct().collect(Collectors.toList());
                result = andSetList;
            }
        }

        List<String> collect = result.stream().map(item -> item.toString()).collect(Collectors.toList());

        return collect;
    }
}
