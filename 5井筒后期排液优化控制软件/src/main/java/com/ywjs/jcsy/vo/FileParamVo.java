package com.ywjs.jcsy.vo;

import com.alibaba.fastjson.annotation.JSONField;
import com.ywjs.jcsy.common.handler.serializerhandler.IsHasNoteSerializable;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * @author zhouwei
 * @create 2020-09-02 17:18
 */
@Data
public class FileParamVo implements Serializable {
    private Integer isMount;
    private Integer managementType;
    private Long tagId;
    private List<Long> tags;
    private String fileName;
    private Long projectId;
    private List<Date> startEndTime;
    private List<String> fileTypes;
    @JSONField(serializeUsing = IsHasNoteSerializable.class, deserializeUsing = IsHasNoteSerializable.class)
    private Boolean isHasNote;         // 是否有笔记
    private List<Integer> progressOf;  // 进度范围
    private List<Integer> score;       // 评分范围
    private List<Long> fileIds;        // 查看指定文件，指定最大查询范围， null，查询所有
}
