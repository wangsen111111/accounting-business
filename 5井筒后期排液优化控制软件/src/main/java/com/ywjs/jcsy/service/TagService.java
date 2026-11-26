package com.ywjs.jcsy.service;

import com.ywjs.jcsy.entity.Tag;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywjs.jcsy.vo.TagScreeningVo;

import java.util.List;
import java.util.Map;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface TagService extends IService<Tag> {

    void deleteTagById(Long tagId);

    List<Map<String, Object>> getHotTagWithFileCount(Long projectId);

    List<String> getTagsByFileId(Long fileId);

    TagScreeningVo tagScreening();

    List<Map<String, Object>> multipleTagMount(List<String> tagNames);

    List<String> singleTagMount(Integer model, List<String> tagNames);
}
