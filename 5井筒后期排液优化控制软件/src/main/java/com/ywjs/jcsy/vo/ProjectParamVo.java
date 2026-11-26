package com.ywjs.jcsy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-08-28 14:27
 */
@Data
public class ProjectParamVo implements Serializable {
    private Integer startProgressOf;
    private Integer endProgressOf;
    private String createUser;
    private String projectName;
    private List<Date> startEndTime;
}
