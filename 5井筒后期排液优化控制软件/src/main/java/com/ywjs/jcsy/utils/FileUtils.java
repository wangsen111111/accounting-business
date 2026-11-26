package com.ywjs.jcsy.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * @author zhouwei
 * @create 2020-11-18 18:41
 */
public class FileUtils {

    /**
     * 递归获取文件
     * @param file
     * @param fileList
     * @return
     */
    public static List<java.io.File> getRecursiveFile(java.io.File file,List<java.io.File> fileList){
        String absolutePath = file.getAbsolutePath();
        java.io.File[] ls = FileUtil.ls(absolutePath);
        for (java.io.File f : ls) {
            boolean isFile = FileUtil.isFile(f);
            if(isFile) fileList.add(f);
            else getRecursiveFile(f,fileList); // 递归
        }
        return fileList;
    }

    /**
     * 移动文件并修改文件名称为真实名称
     * @param targetDir
     * @param files
     */
    public static void moveFileUseRename(String targetDir, List<com.ywjs.jcsy.entity.File> files) {
        if(!CollectionUtil.isEmpty(files)){
            files.forEach(file->{
                if(FileUtil.exist(file.getFilePath())){
                    FileUtil.copy(file.getFilePath(),targetDir,false);
                    // 修改文件名称为真实名称
                    String oldFileName = new File(file.getFilePath()).getName();
                    File newFile = new File(targetDir + File.separator + oldFileName);
                    String realName = file.getFileName() == null ? oldFileName : file.getFileName();
                    newFile.renameTo(new File(targetDir + File.separator + realName));
                }
            });
        }
    }

    public static void moveFileAndRenameExport(String targetDir, List<com.ywjs.jcsy.entity.File> files) {
        if(!CollectionUtil.isEmpty(files)){
            files.forEach(file->{
                // 判断文件是否存在
                if(FileUtil.exist(file.getFilePath())){
                    FileUtil.copy(file.getFilePath(),targetDir,false);
                    // 修改文件名称为真实名称
                    String oldFileName = new File(file.getFilePath()).getName();
                    File newFile = new File(targetDir + File.separator + oldFileName);
                    String realName = file.getFileName() == null ? oldFileName : file.getFileName();
                    realName = UUID.randomUUID().toString().replace("-","")+"_"+realName;
                    newFile.renameTo(new File(targetDir + File.separator + realName));
                }
            });
        }
    }

    public static List<java.io.File> getFiles(String exportFilePath,List<java.io.File> result) {
        List<File> fileList = Arrays.asList(FileUtil.ls(exportFilePath));
        List<File> files = fileList.stream().filter(item -> item.isFile()).collect(Collectors.toList());
        if(!CollectionUtil.isEmpty(files)){
//            List<String> path = files.stream().map(item -> item.getPath()).collect(Collectors.toList());
            result.addAll(files);
        }
        List<File> dirs = fileList.stream().filter(item -> !item.isFile()).collect(Collectors.toList());
        dirs.forEach(item->getFiles(item.getPath(),result));
        return result;
    }
}
