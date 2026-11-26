package com.ywjs.jcsy.service;

import com.ywjs.jcsy.entity.File;
import com.ywjs.jcsy.entity.FileNote;
import com.baomidou.mybatisplus.extension.service.IService;
import com.ywjs.jcsy.vo.NoteTemplateVo;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
public interface FileNoteService extends IService<FileNote> {

    NoteTemplateVo exportNoteByFileId(File file);
}
