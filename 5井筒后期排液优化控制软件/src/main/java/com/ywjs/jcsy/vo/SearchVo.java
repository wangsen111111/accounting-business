package com.ywjs.jcsy.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * @author zhouwei
 * @create 2020-08-03 10:34
 */
@Data
public class SearchVo implements Serializable {
    // 检索条件：文件名、文件类型、标签、项目名、维度、思维导图节点名，扩展根据文件笔记检索（全文检索技术）
    private String  fileName;
    private String fileType;
    private String tagName;
    private String ProjectName;
    private String dimension;
    private String nodeName;
}
