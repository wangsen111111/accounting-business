package com.ywjs.jcsy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-08-05 16:29
 */
@Data
public class MindMapByDimensionVo implements Serializable {
    private String dimensionName;
    private List<MindMapVo> mindMapVos;

    @Data
    class MindMapVo implements Serializable {
        private String mindMapName;
        private List<JsmindVo> jsmindVos;
    }
    @Data
    class JsmindVo implements Serializable {
        private Long id;
        private String topic;
        private Long parentid;
    }

}
