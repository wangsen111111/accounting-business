package com.ywjs.jcsy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zhouwei
 * @create 2020-08-19 14:49
 */
@Getter
public enum IsMountEnum {
    No(0, "未挂载"),
    YES(1, "已挂载");

    @EnumValue
    private int value;
    @JsonValue
    private String desc;

    IsMountEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }

    public static IsMountEnum convert(String desc){
        IsMountEnum[] enums = IsMountEnum.values();
        for(IsMountEnum e : enums){
            if(e.desc == desc)
                return e;
        }
        return null;
    }

    @Override
    public String toString() {
        return desc;
    }
}