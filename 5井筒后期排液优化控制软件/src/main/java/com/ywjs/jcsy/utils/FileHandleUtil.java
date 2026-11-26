package com.ywjs.jcsy.utils;

import cn.hutool.core.io.FileUtil;
import com.ywjs.jcsy.entity.File;
import com.ywjs.jcsy.entity.FileTagRelation;
import com.ywjs.jcsy.entity.Tag;

import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author CZL
 */
public class FileHandleUtil {

    public static String getChildPath(String p1,String p2){
        String str1 = p1.replaceAll("\\\\", "|");
        String str2 = p2.replaceAll("\\\\", "|");
        String childPath = str1.replace(str2, "").replace("|","\\\\");
        return  childPath;
    }

    public static void fileTagPair(List<File> files, List<FileTagRelation> fileTagRelations, List<Tag> tags){
        for (File file : files) {
            List<FileTagRelation> collect = fileTagRelations.stream().filter(fileTagRelation -> fileTagRelation.getFileId() != null && fileTagRelation.getFileId().longValue() == file.getFileId().longValue()).collect(Collectors.toList());
            List<Long> fileTagIds = collect.stream().map(c -> c.getTagId()).collect(Collectors.toList());
            List<Tag> resultTags = tags.stream().filter(tag -> fileTagIds.contains(tag.getTagId())).collect(Collectors.toList());
        }
    }

    /**
     * 递归获取文件
     * @param file
     * @param fileList
     * @return
     */
    public static List<java.io.File> foreachPath(java.io.File file,List<java.io.File> fileList){
        String absolutePath = file.getAbsolutePath();
        java.io.File[] ls = FileUtil.ls(absolutePath);
        for (java.io.File f : ls) {
            boolean isFile = FileUtil.isFile(f);
            if(isFile){
                fileList.add(f);
            }else {
                // 递归
                foreachPath(f,fileList);
            }
        }
        return fileList;
    }
}
