package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.Project;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-09-07 10:32
 */
@Data
public class ProjectBaseInfoVo extends Project implements Serializable {
    private Integer jsmindCount;
    private Integer fileCount;
    private Integer mountOkCount;
    private Integer mountNoCount;
    private Integer tagFileCount;
    private List<Date> startEndTime;
}
