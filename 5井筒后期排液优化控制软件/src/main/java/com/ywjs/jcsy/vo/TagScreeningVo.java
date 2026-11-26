package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.Tag;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 标签筛选vo
 * @author zhouwei
 * @create 2020-10-29 17:35
 */
@Data
public class TagScreeningVo implements Serializable {
    private List<Tag> tags;
    private List<Project3LevelVo> projectScreeningVos;
    private List<String> fileTypes;

//    public static class FileScreeningVo{
//        private String fileType;    // 文件类型
//        private String keyWord;     // 文件名称模糊检索
//        private String progressOf;  // 进度
//        private String score;       // 评分
//    }
}
