package com.ywjs.jcsy.vo;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-08-05 15:36
 */
public class JsmindVo implements Serializable {
    private Long id;
    private String topic;
    private Long parentid;
    private List<JsmindVo> children;
}
