package com.ywjs.jcsy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywjs.jcsy.common.constant.GlobalContstant;
import com.ywjs.jcsy.common.enums.CollectionTypeEnum;
import com.ywjs.jcsy.common.enums.IsMountEnum;
import com.ywjs.jcsy.common.enums.ManagementTypeEnum;
import com.ywjs.jcsy.config.ServerConfig;
import com.ywjs.jcsy.entity.*;
import com.ywjs.jcsy.factory.EntityFactory;
import com.ywjs.jcsy.mapper.FileMapper;
import com.ywjs.jcsy.service.FileProjectRelationService;
import com.ywjs.jcsy.service.FileService;
import com.ywjs.jcsy.service.FileTagRelationService;
import com.ywjs.jcsy.service.TagService;
import com.ywjs.jcsy.utils.*;
import com.ywjs.jcsy.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-07-29
 */
@Slf4j
@Service
@Transactional
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {


    @Autowired
    FileProjectRelationService fileProjectRelationService;

    @Autowired
    FileService fileService;

    @Autowired
    TagService tagService;

    @Autowired
    FileTagRelationService fileTagRelationService;

    @Autowired
    ThreadPoolExecutor executor;

    @Autowired
    ServerConfig serverConfig;

    @Override
    public void downLoad(HttpServletResponse response, List<Long> fileIds, String fileName) {
        String bashPath = GlobalContstant.TMP_PATH+UUID.randomUUID().toString().replace("-","");
        FileUtil.mkdir(bashPath);
//        if (fileIds.size() == 1) {
//            File file = this.baseMapper.selectById(fileIds.get(0));
//            String filePath = file.getFilePath();
//            String[] split = filePath.split("/");
//            fileName = split[split.length - 1];
//            // 浏览器下载文件
//            FileDownloadUtil.fileDownload(response, filePath, fileName,bashPath);
//        } else {
//
//        }

        List<File> files = this.baseMapper.selectBatchIds(fileIds);
        // 过滤不存在的文件
        files = files.stream().filter(file->FileUtil.exist(file.getFilePath())).collect(Collectors.toList());
        if (!CollectionUtil.isEmpty(files)) {
            // 压缩文件
            String tmpPath = bashPath + java.io.File.separator + fileName;
            String movePath = bashPath + java.io.File.separator + "move";
            FileUtil.mkdir(movePath);
            // 将文件拷贝到临时目录
            FileUtils.moveFileUseRename(movePath, files);
            ZipUtil.zip(FileUtil.file(tmpPath), false, FileUtil.ls(movePath));
            // 浏览器下载文件
            FileDownloadUtil.fileDownload(response, tmpPath, fileName,bashPath);
        }



    }

    /**
     * 根据条件检索文件列表 请求参数：文件名、文件类型、标签、项目名、维度、思维导图节点名
     *
     * @param searchVo
     * @return
     */
    @Override
    public List<File> queryFileByParms(SearchVo searchVo) {
        return this.baseMapper.queryFileByParms(searchVo);
    }

    @Override
    public List<String> getFilePathByNodeName(String nodeName) {
        return this.baseMapper.getFilePathByNodeName(nodeName);
    }

    @Override
    public List<Long> getIdsByProjectId(Long projectId) {
//        List<FileNodeRelation> fileNodeRelations = fileNodeRelationService.getListByProjectId(projectId);
//        List<Long> nodeFileIds = fileNodeRelations.stream().map(fileNodeRelation -> fileNodeRelation.getFileId()).collect(Collectors.toList());
//        List<FileProjectRelation> fileProjectRelations = fileProjectRelationService.list(new LambdaQueryWrapper<FileProjectRelation>().select(FileProjectRelation::getFileId).eq(FileProjectRelation::getProjectId, projectId));
//        List<Long> fileIds = fileProjectRelations.stream().map(fileProjectRelation -> fileProjectRelation.getFileId()).collect(Collectors.toList());
//        fileIds.addAll(nodeFileIds);
//        return fileIds;
        return null;
    }

    @Override
    public List<File> getFileDetailedByIds(List<Long> fileIds) {
        List<FileTagRelation> fileTagRelations = fileTagRelationService.getListByFileIds(fileIds);
        List<Long> tagIds = fileTagRelations.stream().map(fileTagRelation -> fileTagRelation.getTagId()).collect(Collectors.toList());
        List<Tag> tags = tagService.listByIds(tagIds);
        List<File> files = fileService.listByIds(fileIds);
        FileHandleUtil.fileTagPair(files, fileTagRelations, tags);
        return files;
    }

    @Override
    public List<FilesVo> getFileByParam(FileParamVo parmMap) {
        LambdaQueryWrapper<File> queryWrapper = getLambdaQueryWrapper(parmMap);
        List<File> files = this.list(queryWrapper);
        List<FilesVo> vos = files.stream().map(file -> {
            FilesVo filesVo = new FilesVo();
            BeanUtil.copyProperties(file, filesVo);
            Long fileId = file.getFileId();
            filesVo.setKey(fileId.toString());
            return filesVo;
        }).collect(Collectors.toList());
        return vos;
        // 获取所有项目名称
//        List<Long> fileIds = files.stream().map(File::getFileId).collect(Collectors.toList());
//        // 文件id-项目id
//        List<FileProjectRelation> fileProjectRelations = new FileProjectRelation().selectList(new LambdaQueryWrapper<FileProjectRelation>().in(FileProjectRelation::getFileId,fileIds));
//
//        // 获取项目名称
//        List<Long> projectId = fileProjectRelations.stream().map(FileProjectRelation::getProjectId).distinct().collect(Collectors.toList());
//        List<Project> projects = new Project().selectList(new LambdaQueryWrapper<Project>().in(Project::getProjectId, projectId));

//        List<FilesVo> filesVoList = files.stream().map(file -> {
//            FilesVo filesVo = new FilesVo();
//            BeanUtil.copyProperties(file,filesVo);
//            // 获取项目名称
//            Long fileId = file.getFileId();
//            List<FileProjectRelation> collect = fileProjectRelations.stream().filter(item -> item.getFileId().equals(fileId)).collect(Collectors.toList());
//            if(!CollectionUtil.isEmpty(collect)){
//                FileProjectRelation filter = collect.get(0);
//                Project project = projects.stream().filter(item -> item.getProjectId().equals(filter.getProjectId())).collect(Collectors.toList()).get(0);
//                filesVo.setProjectName(project.getProjectName());
//            }
//            return filesVo;
//        }).collect(Collectors.toList());

//        return files;
    }

    /**
     * 根据插叙条件组装查询条件
     * @param parmMap
     * @return
     */
    private LambdaQueryWrapper<File> getLambdaQueryWrapper(FileParamVo parmMap){
        LambdaQueryWrapper<File> queryWrapper = new LambdaQueryWrapper<>();

        List<Integer> progressOf = parmMap.getProgressOf();
        if(!CollectionUtil.isEmpty(progressOf)){
            queryWrapper.ge(File::getProgressOf,progressOf.get(0)).le(File::getProgressOf,progressOf.get(1));
        }

        List<Integer> score = parmMap.getScore();
        if(!CollectionUtil.isEmpty(score)){
            queryWrapper.ge(File::getScore,score.get(0)).le(File::getScore,score.get(1));
        }

        Long tagId = parmMap.getTagId();
        if(tagId != null){
            List<FileTagRelation> fileTagRelations = EntityFactory.create(FileTagRelation.class).selectList(new LambdaQueryWrapper<FileTagRelation>().eq(FileTagRelation::getTagId, tagId));
            List<Long> fileIds = fileTagRelations.stream().map(item -> item.getFileId()).collect(Collectors.toList());
            queryWrapper.in(File::getFileId,fileIds);
        }


        List<Long> fileIdsMaxRange = parmMap.getFileIds();
        if(fileIdsMaxRange != null){ // 查看全部文件，[]时查看查看问空
            queryWrapper.in(File::getFileId,fileIdsMaxRange);
        }

        Long projectId = parmMap.getProjectId();
        List<Long> tagIds = parmMap.getTags();
        if(!CollectionUtil.isEmpty(tagIds)){
            List<FileTagRelation> fileTagRelations = EntityFactory.create(FileTagRelation.class).selectList(new LambdaQueryWrapper<FileTagRelation>().in(FileTagRelation::getTagId, tagIds));
            List<Long> fileIds = fileTagRelations.stream().map(item -> item.getFileId()).collect(Collectors.toList());
            queryWrapper.in(File::getFileId,fileIds);
        }
        if(projectId != null && CollectionUtil.isEmpty(fileIdsMaxRange)){
            List<FileProjectRelation> fileProjectRelations = EntityFactory.create(FileProjectRelation.class).selectList(new LambdaQueryWrapper<FileProjectRelation>().eq(FileProjectRelation::getProjectId, projectId));
            List<Long> fileIds = fileProjectRelations.stream().map(item -> item.getFileId()).collect(Collectors.toList());
            queryWrapper.in(File::getFileId,fileIds);
        }

//        if(!CollectionUtil.isEmpty(tagIds) && projectId != null){
//            List<FileProjectRelation> fileProjectRelations = EntityFactory.create(FileProjectRelation.class).selectList(new LambdaQueryWrapper<FileProjectRelation>().eq(FileProjectRelation::getProjectId, projectId));
//            List<Long> fileIdsByProjectId = fileProjectRelations.stream().map(item -> item.getFileId()).collect(Collectors.toList());
//            List<FileTagRelation> fileTagRelations = EntityFactory.create(FileTagRelation.class).selectList(new LambdaQueryWrapper<FileTagRelation>().in(FileTagRelation::getTagId, tagIds));
//            List<Long> fileIdsByTagId = fileTagRelations.stream().map(item -> item.getFileId()).collect(Collectors.toList());
//            List<Long> fileIds = fileIdsByTagId.stream().filter(item -> fileIdsByProjectId.contains(item)).collect(Collectors.toList());
//            queryWrapper.in(File::getFileId,fileIds);
//        }else {
//
//        }

        if(!StrUtil.isEmpty(parmMap.getFileName())){
            queryWrapper.like(File::getFileName,parmMap.getFileName());
        }
        Integer managementType = parmMap.getManagementType();
        if(managementType != null){
            if(managementType == 0){
                queryWrapper.eq(File::getManagementType,ManagementTypeEnum.OUTER_CHAIN);
            }else if (managementType == 1){
                queryWrapper.eq(File::getManagementType,ManagementTypeEnum.UPLOAD);
            }
        }
        List<Date> startEndTime = parmMap.getStartEndTime();
        if(!CollectionUtil.isEmpty(startEndTime)){
            queryWrapper.ge(File::getUploadTime,startEndTime.get(0)).le(File::getUploadTime,startEndTime.get(1));
        }
        List<String> fileTypes = parmMap.getFileTypes();
        if(!CollectionUtil.isEmpty(fileTypes)){
            queryWrapper.in(File::getFileType,fileTypes);
        }
        Boolean isHasNote = parmMap.getIsHasNote();
        if(isHasNote != null){
            if(isHasNote){
                // 获取所有有文件笔记
                queryWrapper.apply("file_id in (SELECT distinct file_id FROM file_note where file_id notnull)");
            }else {
                // 获取所有无文件笔记
                queryWrapper.apply("file_id not in (SELECT distinct file_id FROM file_note where file_id notnull)");
            }
        }
        return queryWrapper;
    }

    @Override
    public PageVo<FilesVo> getFileDetail(Integer pageNum, Integer pageCount, FileParamVo parmMap) {
        // 组装查询条件
        LambdaQueryWrapper<File> queryWrapper = getLambdaQueryWrapper(parmMap);
        Page<File> page = new Page<>(pageNum, pageCount);

        IPage<File> fileIPage = fileService.page(page, queryWrapper.orderByDesc(File::getUploadTime));
        List<File> records = fileIPage.getRecords();

        Integer isMount = parmMap.getIsMount();
        // 获取文件总数量
        Long total = fileIPage.getTotal();
        if(isMount != null){
            if(isMount == 0){ // 未挂载
                int beforeFilter = records.size();
                records = records.stream().filter(item-> item.getIsMount().getValue() == IsMountEnum.No.getValue()).collect(Collectors.toList());
                int afterFilter = records.size();
                // 计算总数量
                total -= (beforeFilter-afterFilter);

            }else if (isMount == 1){ // 挂载
                int beforeFilter = records.size();
                records = records.stream().filter(item-> item.getIsMount().getValue() == IsMountEnum.YES.getValue()).collect(Collectors.toList());
                int afterFilter = records.size();
                // 计算总数量
                total -= (beforeFilter-afterFilter);
            }
        }

        // 转化为FilesVo
        List<FilesVo> vos = records.stream().map(item -> {
            FilesVo filesVo = new FilesVo();
            BeanUtil.copyProperties(item, filesVo);

            // 使用CompletableFuture 异步执行
            CompletableFuture<Void> getIsHasNote = CompletableFuture.runAsync(() -> {
                // 查询当前文件是否有笔记 SELECT distinct file_id FROM file_note where file_id notnull
                List<FileNote> fileNotes = new FileNote().selectList(new LambdaQueryWrapper<FileNote>().eq(FileNote::getFileId, item.getFileId()));
                if (!CollectionUtil.isEmpty(fileNotes)) {
                    // 获取所有有文件笔记
                    filesVo.setIsHasNote(true);
                }
            }, executor);

            // 获取文件标签
            CompletableFuture<Void> getTags = CompletableFuture.runAsync(() -> {
                List<String> tags = tagService.getTagsByFileId(filesVo.getFileId());
                filesVo.setTags(tags);
                filesVo.setKey(filesVo.getFileId().toString());
            }, executor);

            // 获取项目数
            CompletableFuture<Void> getProjectNumber = CompletableFuture.runAsync(() -> {
                List<FileProjectRelation> fileProjectRelations = new FileProjectRelation().selectList(new LambdaQueryWrapper<FileProjectRelation>().eq(FileProjectRelation::getFileId, item.getFileId()));
                // 获取项目数
                if(CollectionUtil.isEmpty(fileProjectRelations)){
                    filesVo.setProjectNumber(0);
                }else {
                    filesVo.setProjectNumber(fileProjectRelations.size());
                    // 组装 filesVo.Info
                    List<FilesVo.Info> infos = new ArrayList<>();
                    // 获取改文件所在思维导图
                    for (FileProjectRelation fp : fileProjectRelations) {
                        // 获取项目
                        String projectName = "";
                        if(fp.getProjectId() != null){
                            Project project = new Project().setProjectId(fp.getProjectId()).selectById();
                            if(project != null){
                                projectName = project.getProjectName();
                            }
                        }
                        List<Jsmind> jsminds = new Jsmind().selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getProjectId, fp.getProjectId()));
                        if(!CollectionUtil.isEmpty(jsminds)){
                            for (Jsmind jsmind : jsminds) {
                                String mindMapName = jsmind.getMindMapName();
                                JsmindDataVo.JsMindData data = jsmind.getData().getData();
                                List<JsmindDataVo.JsMindData> jsMindDataArrayList = new ArrayList<>();
                                jsMindDataArrayList.add(data);
                                // 查寻文件id所在节点及其路径
                                Map<String, String> map = new HashMap<>();
                                foreachJsMindData(jsMindDataArrayList,filesVo.getFileId(),map);
                                if(!CollectionUtil.isEmpty(map)){
                                    for (Map.Entry<String, String> entry : map.entrySet()) {
                                        FilesVo.Info info = new FilesVo.Info();
                                        info.setJsmindId(jsmind.getId());
                                        info.setProjectName(projectName);
                                        info.setMindMapName(mindMapName);
                                        info.setKey(entry.getValue());
                                        info.setNodeName(entry.getKey());
//                                    info.setPath(entry.getValue());
                                        infos.add(info);
                                    }
                                }
                            }
                        }
                    }
                    filesVo.setInfos(infos);
                }
            }, executor);

            // 等异步任务完
            try {
                CompletableFuture.allOf(getIsHasNote,getTags,getProjectNumber).get();
            } catch (InterruptedException e) {
                log.error(PrintUtil.printStackTrace(e));
//                throw new RuntimeException(PrintUtil.printStackTrace(e));
            } catch (ExecutionException e) {
                log.error(PrintUtil.printStackTrace(e));
//                throw new RuntimeException(PrintUtil.printStackTrace(e));
            }
            return  filesVo;
        }).collect(Collectors.toList());
        // 组织成分页vo

        PageVo<FilesVo> pageVo = new PageVo<FilesVo>().setData(vos).setPageNum(pageNum).setPageSize(pageCount).setTotal(total.intValue());
        return pageVo;
    }

    /**
     * 获取文件所在节点及其路径
     * @param data
     * @param fileId
     */
    private void foreachJsMindData(List<JsmindDataVo.JsMindData> data, Long fileId, Map<String,String> map) {
        data.forEach(item->{
            String topic = item.getTopic();
            String nodeId = item.getId();
            List<Long> fileIds = item.getFileIds();
            if(!CollectionUtil.isEmpty(fileIds)){
                if(fileIds.contains(fileId)){
                    map.put(topic,nodeId);
                }
            }
            // 递归条件
            List<JsmindDataVo.JsMindData> children = item.getChildren();
            if(children != null){
                foreachJsMindData(children,fileId,map);
            }
        });

    }

    @Override
    public void deleteFileByIds(List<Long> ids) {
        // 查询文件
        List<File> files = this.baseMapper.selectBatchIds(ids);
        List<String> filePaths = files.stream().map(file -> file.getFilePath()).collect(Collectors.toList());
        // 删除标签关联表
        new FileTagRelation().delete(new LambdaQueryWrapper<FileTagRelation>().in(FileTagRelation::getFileId, ids));
        // 删除项目关联表
        new FileProjectRelation().delete(new LambdaQueryWrapper<FileProjectRelation>().in(FileProjectRelation::getFileId, ids));
        // 删除笔记关联表
        new FileNote().delete(new LambdaQueryWrapper<FileNote>().in(FileNote::getFileId, ids));
        // 删除收藏表
        List<String> idsStr = ids.stream().map(id -> id.toString()).collect(Collectors.toList());
        new CollectionSc().delete(new LambdaQueryWrapper<CollectionSc>().in(CollectionSc::getTargetId,ids).eq(CollectionSc::getCollectionType, CollectionTypeEnum.FILE));
        // 删除文件
        fileService.removeByIds(ids);
        // 删除服务路径文件
        if(!CollectionUtil.isEmpty(filePaths)){
            filePaths.forEach(path->FileUtil.del(new java.io.File(path)));
        }
    }


    @Override
    public void editFileTag(List<String> tagNames, Long fileId) {
        // 删除文件标签
        new FileTagRelation().delete(new LambdaQueryWrapper<FileTagRelation>().eq(FileTagRelation::getFileId, fileId));
        // 查收所有标签
        List<String> dbTags = tagService.list().stream().map(tag -> tag.getTagName()).collect(Collectors.toList());
        // 查看需新添加标签
        List<String> addTags = tagNames.stream().filter(item -> !dbTags.contains(item)).collect(Collectors.toList());

        if (!CollectionUtil.isEmpty(addTags)) {
            // 插入新标签
            List<Tag> inseatTags = addTags.stream().map(tagName -> new Tag().setTagName(tagName)).collect(Collectors.toList());
            tagService.saveBatch(inseatTags);
        }
        // 查收需绑定标签id
        List<Tag> tags = tagService.list(new LambdaQueryWrapper<Tag>().in(Tag::getTagName, tagNames));
        tags.forEach(tag -> new FileTagRelation().setFileId(fileId).setTagId(tag.getTagId()).insert());
    }

    @Override
    public PageVo<File> getPageFileByProjectId(Integer pageNum, Integer pageSize, Integer projectId, String fileName) {
//        files = this.baseMapper.getPageFileByProjectId(pageNum, pageSize, projectId, fileName);
//        total = this.baseMapper.getFileCountByProjectId(projectId, fileName);

        LambdaQueryWrapper<File> wrapper = new LambdaQueryWrapper<>();
        if(projectId != null){
            List<FileProjectRelation> fp = new FileProjectRelation().selectList(new LambdaQueryWrapper<FileProjectRelation>().eq(FileProjectRelation::getProjectId, projectId));
            List<Long> fileIds = fp.stream().map(item -> item.getFileId()).collect(Collectors.toList());
            wrapper.in(File::getFileId,fileIds);
        }
        if(fileName != null){
            wrapper.like(File::getFileName,fileName);
        }

        IPage<File> fileIPage = new File().selectPage(new Page<>(pageNum, pageSize), wrapper.orderByAsc(File::getUploadTime));
        List<File> files = fileIPage.getRecords();
        Long total = fileIPage.getTotal();
        return new PageVo<File>().setPageSize(pageSize).setPageNum(pageNum).setData(files).setTotal(total.intValue());
    }

    @Override
    public Map<String, List<File>> fileUpload(MultipartFile[] files) {
        return fileUpload(files, null);
    }

    @Override
    public void downloadByMindMap(HttpServletResponse response, Jsmind jsmind) {
        downloadMindMapFiles(response, jsmind);
    }

    @Override
    public List<String> getFileTypes() {
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.select("distinct file_type");
        List<File> files = fileService.getBaseMapper().selectList(wrapper);
        if(CollectionUtil.isEmpty(files)){
            return null;
        }else {
            List<String> fileTypes = files.stream().filter(file->file!=null).map(File::getFileType).collect(Collectors.toList());
            return fileTypes;
        }
    }



    /**
     * 根据思维导图结构文件
     * @param response
     * @param jsmind
     */
    private void downloadMindMapFiles(HttpServletResponse response, Jsmind jsmind) {
        // 根据思维导图创建临时目录
//        String currProjectPath = System.getProperty("user.dir") ;
//        FileUtil.mkdir(currProjectPath);
        JsmindDataVo.JsMindData data = jsmind.getData().getData();

        String basePath = GlobalContstant.TMP_PATH+UUID.randomUUID().toString().replace("-","")+ java.io.File.separator;
        FileUtil.mkdir(basePath);

        JsmindUtil.copyFileUseRealNameByJsmind(data,basePath,fileService);
        // 压缩临时文件
        // 获取根节点名称
        String topic = data.getTopic();
        // 去除html片段和后缀
        topic = topic.replaceAll("[(]{1}[0-9]+[)]","");
        topic = topic.substring(topic.lastIndexOf(">")+1,topic.length());
        String zipPath = basePath + topic + ".zip";
        ZipUtil.zip(basePath+topic, zipPath, true);
        // 浏览器下载文件
        FileDownloadUtil.fileDownload(response, zipPath, topic,basePath);
    }

    @Override
    public Map<String, List<File>> fileUpload(MultipartFile[] files, Long projectId) {
        // 获取项目名称
        Project project = null;
        if(projectId != null){ project = new Project().setProjectId(projectId).selectById(); }

        List<File> successFile = new ArrayList<>();
        List<File> failFile = new ArrayList<>();
        List<File> existFile = new ArrayList<>();


        for (MultipartFile file : files) {
            // 上传简单文件名
            String originalFilename = file.getOriginalFilename();
            int index;
            if ((index = originalFilename.lastIndexOf("/")) > -1) {
                originalFilename = originalFilename.substring(index + 1);
            }
            // 切分文件名和后缀
            String fileType = originalFilename.substring(originalFilename.lastIndexOf(".") + 1, originalFilename.length());
            String buildName = UUID.randomUUID().toString().replace("-","")+"."+fileType;
            // 保存文件到服务本地
            String filePath = GlobalContstant.UPLOAD_PATH+  buildName;
            try {
                // 判断该文件是否已存在
                if (FileUtil.exist(filePath)) {
                    existFile.add(new File().setFileName(originalFilename));  // 文件已存在
                } else {
                    file.transferTo(new java.io.File(filePath)); // 需上传文件
                    // 保存该文件数据库记录
                    // 获取服务器地址
                    String childPath = FileHandleUtil.getChildPath(filePath, GlobalContstant.UPLOAD_BASE_PATH);
                    String accessPath = serverConfig.getAccessPath(childPath);
                    File upFile = new File().setFilePath(filePath).setFileName(originalFilename)
                            .setManagementType(ManagementTypeEnum.UPLOAD).setFileType(fileType).setIsMount(IsMountEnum.No);
                    upFile.setAccessPath(accessPath);
                    successFile.add(upFile); // 成功上传文件
                }
            } catch (IOException e) {
                e.printStackTrace();
                // 上传异常文件
                failFile.add(new File().setFileName(originalFilename));
            }
        }
        try {
            // 将文件集合批量入库
            fileService.saveBatch(successFile);
            // 文件项目关联表批量入库
            List<FileProjectRelation> fileProjectRelations = successFile.stream().map(item -> new FileProjectRelation().setProjectId(projectId).setFileId(item.getFileId())).collect(Collectors.toList());
            fileProjectRelationService.saveBatch(fileProjectRelations);
        } catch (Exception e) {
            // 数据库保存异常，将上传文件删除
            successFile.forEach(file-> FileUtil.del(file.getFilePath()) );
        }

        // 组织返回数据
        Map<String, List<File>> resultMap = new HashMap<>();
        resultMap.put("success", successFile);
        resultMap.put("fail", failFile);
        resultMap.put("exist", existFile);
        return resultMap;
    }

    @Override
    public List<File> filChain(String pathString, Long projectId) {
        // 分割
        String[] paths = pathString.split("\n");
        List<java.io.File> fileList = new ArrayList<>();
        for (String path : paths) {
            if (FileUtil.isFile(path)) {
                fileList.add(new java.io.File(path));
            } else {
                // 获取文件下文件
                List<java.io.File> fileListDir = FileUtils.getRecursiveFile(new java.io.File(path), new ArrayList<java.io.File>());
                fileList.addAll(fileListDir);
            }
        }
        List<File> fileChains = fileList.stream().map(file -> {
            String path = file.getAbsolutePath();
            // 将文件外链到数据库
            String[] split = path.split("\\\\");
            String originalFilename = split[split.length - 1];
            int lastIndexOf = originalFilename.lastIndexOf(".");
            String fileType = originalFilename.substring(lastIndexOf + 1, originalFilename.length());
            File fileChain = new File().setIsMount(IsMountEnum.No).setUploadTime(new Date()).setFilePath(path)
                    .setManagementType(ManagementTypeEnum.OUTER_CHAIN).setFileName(originalFilename).setFileType(fileType);
            return fileChain;
        }).collect(Collectors.toList());
        saveBatch(fileChains);
        if(projectId != null){
            List<FileProjectRelation> fileProjectRelations = fileChains.stream().map(item -> new FileProjectRelation().setFileId(item.getFileId()).setProjectId(projectId) ).collect(Collectors.toList());
            fileProjectRelationService.saveBatch(fileProjectRelations);
        }
        return fileChains;
    }

    @Override
    public List<Map<String, Object>> countFileType(Long projectId) {
        return this.baseMapper.countFileType(projectId);
    }

    @Override
    public Integer tagFileCount(Long projectId) {
        return this.baseMapper.tagFileCount( projectId);
    }




}
