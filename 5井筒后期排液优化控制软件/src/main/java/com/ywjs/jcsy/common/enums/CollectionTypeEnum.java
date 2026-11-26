package com.ywjs.jcsy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zhouwei
 * @create 2020-08-13 18:39
 */
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
@Getter
//@JSONType(SerializeEnumAsJavaBean = true)
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum CollectionTypeEnum /*implements IEnum<Integer>*/ {
    FILE(0, "文件表"),
    MIND_MAP(1, "思维导图表"),
    PROJECT(2,"项目表");
    @EnumValue
    private int value;
    @JsonValue
    private String desc;

    CollectionTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

}