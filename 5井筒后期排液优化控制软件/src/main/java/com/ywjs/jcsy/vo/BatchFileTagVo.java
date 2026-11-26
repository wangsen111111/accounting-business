package com.ywjs.jcsy.vo;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-12-16 11:21
 */
@Data
public class BatchFileTagVo implements Serializable {
    private List<Long> fileIds;
    private List<String> tagNames;
}
