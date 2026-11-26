package com.ywjs.jcsy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouwei
 * @create 2020-11-05 16:13
 */
@Data
public class AssociatedIdVo implements Serializable {
    private Long projectId;
    private Long dimensionId;
    private String jsmindId;                      // 主键作为思维导图容器id
}
