package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.Dimension;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.entity.Project;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 思维导图关联vo
 * @author zhouwei
 * @create 2020-11-05 11:07
 */
@Data
public class AssociatedVo implements Serializable {

    private List<JsmindAssociatedVo> jsmindAssociatedVos;
    private List<ProjectVo> projects;
    private List<Dimension> dimensions;

    @Data
    public static class JsmindAssociatedVo{
        private JsmindVo jsmind = new JsmindVo();
        private ProjectVo project = new ProjectVo();
        private DimensionVo dimension = new DimensionVo();
    }

    @Data
    public static class DimensionVo{
        private Long dimensionId;
        private String dimensionName;
    }

    @Data
    public static class JsmindVo{
        private String id;                      // 主键作为思维导图容器id
        private String mindMapName;             // 思维导图名称
    }

    @Data
    public static class ProjectVo{
        private Long projectId;             // 项目id
        private String projectName;         // 项目名称
    }
}
