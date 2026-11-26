package com.ywjs.jcsy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouwei
 * @create 2020-07-31 11:24
 */
@Data
public class FileUploadLocVo implements Serializable {
    private String filePath;
    private Long nodeId;
}