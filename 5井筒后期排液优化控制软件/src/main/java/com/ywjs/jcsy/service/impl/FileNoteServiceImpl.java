package com.ywjs.jcsy.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.ywjs.jcsy.entity.File;
import com.ywjs.jcsy.entity.FileNote;
import com.ywjs.jcsy.mapper.FileNoteMapper;
import com.ywjs.jcsy.service.FileNoteService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywjs.jcsy.vo.NoteTemplateVo;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
@Service
@Transactional
public class FileNoteServiceImpl extends ServiceImpl<FileNoteMapper, FileNote> implements FileNoteService {

    @Override
    public NoteTemplateVo exportNoteByFileId(File file) {
        // 根据文件id查询笔记
        List<FileNote> fileNotes = this.list(new LambdaQueryWrapper<FileNote>().eq(FileNote::getFileId, file.getFileId()));
        // 组织笔记
        NoteTemplateVo noteTemplateVo = new NoteTemplateVo();
        noteTemplateVo.setFile(file);
        noteTemplateVo.setFileNotes(fileNotes);
        return noteTemplateVo;
    }

}
