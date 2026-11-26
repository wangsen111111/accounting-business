package com.ywjs.jcsy.common.constant;

import lombok.Getter;

/**
 * @author zhouwei
 * @create 2020-12-17 15:58
 */
@Getter
public enum DimensionEnum {
    TYPE("类型维度"),
    EVENT("事件维度"),
    TIME("时间维度");



    private String value;

    DimensionEnum(String value) {
        this.value = value;
    }

    @Override
    public String toString() {
        return value;
    }

}