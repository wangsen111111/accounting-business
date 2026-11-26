package com.ywjs.jcsy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouwei
 * @create 2020-09-07 14:11
 */
@Data
public class JsmindTagVo implements Serializable {
    private String tagName;
    private String mindMapName;
    private Integer count;
}
