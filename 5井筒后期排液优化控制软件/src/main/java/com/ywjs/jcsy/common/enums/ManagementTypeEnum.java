package com.ywjs.jcsy.common.enums;

import com.baomidou.mybatisplus.annotation.EnumValue;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * @author zhouwei
 * @create 2020-08-13 16:53
 */
@Getter
public enum ManagementTypeEnum /*implements IEnum<Integer>*/ {
    OUTER_CHAIN(0, "外链"),
    UPLOAD(1, "上传");

    @EnumValue
    private int value;
    @JsonValue
    private String desc;

    ManagementTypeEnum(Integer value, String desc) {
        this.value = value;
        this.desc = desc;
    }
    public static ManagementTypeEnum convert(String desc){
        ManagementTypeEnum[] enums = ManagementTypeEnum.values();
        for(ManagementTypeEnum e : enums){
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