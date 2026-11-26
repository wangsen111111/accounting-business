package com.ywjs.jcsy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author zhouwei
 * @create 2020-08-21 14:55
 * 是否启用编辑
 */
public enum EditableEnum {
    No(0, "不启用编辑"),
    YES(1, "启用编辑");

    @EnumValue
    private int value;
    @JsonValue
    private String desc;

    EditableEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

}
