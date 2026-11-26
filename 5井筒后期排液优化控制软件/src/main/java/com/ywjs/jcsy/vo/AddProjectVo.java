package com.ywjs.jcsy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-07-31 14:29
 */
@Data
public class AddProjectVo implements Serializable {
    private String projectName; // 项目名
    private String note; // 项目简介
    private List<Model> models;

    @Data
    public static class Model{
        private String dimension; // 项目维度
        private Integer createModel; // 创建模式 0：创建空项目 1：选择模板创建 2：已有项目创建
        private String modelName; // 模板名，创建模式 0：null ，1：模板名 2：项目名
    }
}
