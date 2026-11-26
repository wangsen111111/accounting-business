package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.Project;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-08-28 15:37
 */
@Data
public class ProjectVo extends Project implements Serializable {
    private Integer mountNoCount;
    private Integer mountOkCount;
    private Integer tagFileCount;
    private Integer fileCount;
    private Integer jsmindCount;
    private String startStopTime;
    private Long key; // 主键
}
