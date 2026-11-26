package com.ywjs.jcsy.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

/**
 * 项目三级分类
 * @author zhouwei
 * @create 2020-10-29 18:19
 */
@Data
@Accessors(chain = true)
public class Project3LevelVo implements Serializable {
    private String title; // 标题，1级对应项目名称，2级对应思维导图名称，3级对应节点名称
//    private String value; // 该节点数据,
    private String value;
    private String key;   // 树节点id，123级分别对应项目id、思维导图Id、节点id，防重拼接前缀分别为project、jsminf、node
    private List<Project3LevelVo> children;

    @Data
//    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    @Accessors(chain = true)
    public static class Value{
        private String projectId;
        private String jsmindId;
        private String nodeId;
    }
}
