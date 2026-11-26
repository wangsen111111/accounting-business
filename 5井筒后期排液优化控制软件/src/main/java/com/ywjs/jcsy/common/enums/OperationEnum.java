package com.ywjs.jcsy.common.enums;


import lombok.Getter;

/**
 * @author zhouwei
 * @create 2020-09-09 15:50
 */
@Getter
public enum OperationEnum {
    FILE("file", "文件表"),
    JSMIND("jsmind", "思维导图表"),
    PROJECT("project","项目表");

    private String value;
    private String desc;
    OperationEnum(String value, String desc) {
        this.value = value;
        this.desc = desc;
    }
}
