package com.ywjs.jcsy.vo;

import com.ywjs.jcsy.entity.File;
import com.ywjs.jcsy.entity.FileNote;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 笔记模板
 * @author zhouwei
 * @create 2020-11-06 14:17
 */
@Data
public class NoteTemplateVo implements Serializable {
    private File file;
    private List<FileNote> fileNotes;
}
