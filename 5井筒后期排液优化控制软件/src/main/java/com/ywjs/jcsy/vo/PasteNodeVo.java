package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.Jsmind;
import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouwei
 * @create 2020-11-09 14:56
 */
@Data
public class PasteNodeVo implements Serializable {

    private Jsmind jsmind;
    private JsmindDataVo.JsMindData copyNode;
    private String copyNodeId;

}
