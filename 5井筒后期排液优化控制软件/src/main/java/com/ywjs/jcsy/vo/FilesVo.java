package com.ywjs.jcsy.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.ywjs.jcsy.common.handler.serializerhandler.IsHasNoteSerializable;
import com.ywjs.jcsy.entity.File;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-08-13 10:57
 */
@Data
public class FilesVo  extends File implements Serializable {

    private Integer projectNumber;
    private String key; // 文件id
    private List<Info> infos;
    private List<String> tags;
    private String projectName;
    @JSONField(serializeUsing = IsHasNoteSerializable.class, deserializeUsing = IsHasNoteSerializable.class)
    private Boolean isHasNote = false;  // 是否有笔记

    /**
     * 文件额外信息
     */
    @Data
    public static class Info{
        private String key; // 所在节点id
        private String jsmindId; // 思维导图id
        private String projectName;
        private String mindMapName;
        private String nodeName;
    }
}
