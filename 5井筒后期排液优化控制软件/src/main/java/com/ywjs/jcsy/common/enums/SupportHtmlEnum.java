package com.ywjs.jcsy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;

/**
 * @author zhouwei
 * @create 2020-08-21 14:52
 * 是否支持节点里的HTML元素
 */
public enum  SupportHtmlEnum {

    No(0, "不支持节点里的HTML元素"),
    YES(1, "支持节点里的HTML元素");

    @EnumValue
    private int value;
    @JsonValue
    private String desc;

    SupportHtmlEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    @Override
    public String toString() {
        return desc;
    }

}
