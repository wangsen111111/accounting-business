package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.Jsmind;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouwei
 * @create 2020-09-25 9:49
 */
@Data
public class AddJsmindVo implements Serializable {

    private Long projectId;
    private String mindMapName;
    private Long dimensionId;
    private Jsmind jsmind;
    private Boolean isEmpty;

}
