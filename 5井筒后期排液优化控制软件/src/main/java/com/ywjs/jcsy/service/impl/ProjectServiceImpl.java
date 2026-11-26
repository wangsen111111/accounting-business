package com.ywjs.jcsy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywjs.jcsy.common.constant.GlobalContstant;
import com.ywjs.jcsy.common.enums.CollectionTypeEnum;
import com.ywjs.jcsy.common.enums.IsMountEnum;
import com.ywjs.jcsy.common.enums.ManagementTypeEnum;
import com.ywjs.jcsy.common.enums.OperationEnum;
import com.ywjs.jcsy.common.result.ResultCode;
import com.ywjs.jcsy.config.ServerConfig;
import com.ywjs.jcsy.entity.*;
import com.ywjs.jcsy.exception.GlobalException;
import com.ywjs.jcsy.mapper.JsmindMapper;
import com.ywjs.jcsy.mapper.ProjectMapper;
import com.ywjs.jcsy.service.CollectionService;
import com.ywjs.jcsy.service.FileProjectRelationService;
import com.ywjs.jcsy.service.FileService;
import com.ywjs.jcsy.service.ProjectService;
import com.ywjs.jcsy.utils.FileDownloadUtil;
import com.ywjs.jcsy.utils.JsmindUtil;
import com.ywjs.jcsy.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

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
public class ProjectServiceImpl extends ServiceImpl<ProjectMapper, Project> implements ProjectService {

    @Value("${file.staticAccessPath}")
    private String staticAccessPath;

    @Autowired
    FileService fileService;
    @Autowired
    CollectionService collectionService;

    @Autowired
    FileProjectRelationService fileProjectRelationService;

    @Autowired
    ServerConfig serverConfig;

    @Autowired
    JsmindMapper jsmindMapper;

    @Transactional
    @Override
    public void  outputProject(Long id, HttpServletResponse response) {
        // 根据思维导图创建临时目录
//        String currProjectPath = System.getProperty("user.dir") ;
//        FileUtil.mkdir(currProjectPath);
        // 获取当前项目名称
        Project project = new Project().setProjectId(id).selectById();
        // 获取当前思维导图
        List<Jsmind> jsminds = new Jsmind().selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getProjectId, id));
        String bashPath = GlobalContstant.TMP_PATH + UUID.randomUUID().toString().replace("-","")+File.separator;
        String projectPath = bashPath + project.getProjectName();
        FileUtil.mkdir(projectPath);
        jsminds.forEach(jsmind->{
            JsmindDataVo.JsMindData data = jsmind.getData().getData();
            JsmindUtil.copyFileByJsmind(data,projectPath,fileService);
        });
        // 压缩临时文件
        String zipPath = bashPath + project.getProjectName() + ".zip";
        ZipUtil.zip(projectPath, zipPath, true);
        // 浏览器下载
        FileDownloadUtil.fileDownload(response, zipPath, "项目压缩包.zip",bashPath);
    }



    @Override
    public Map<String, List> importProjects(MultipartFile[] files,Integer saveType){
        List<String> projectNams = Arrays.asList(FileUtil.ls(GlobalContstant.UPLOAD_BASE_PATH)).stream().map(file -> file.getName()).collect(Collectors.toList());
        List<String> ok = new ArrayList<>();
        List<String> no = new ArrayList<>();
        // 校验是否为.zip文件
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            if(!(originalFilename.contains(".zip"))){
                throw new GlobalException(ResultCode.FILE_CHECK_ERROR);
            }
        }

        List<String> paths = new ArrayList<>();
        for (MultipartFile file : files) {
            String originalFilename = file.getOriginalFilename();
            // 获取项目名称
            String projectName = originalFilename.substring(0,originalFilename.lastIndexOf("."));
            if(projectNams.contains(projectName)){
                no.add(projectName); // 该导入项目已存在
            }else {
                try {
                    InputStream in = file.getInputStream(); // 解压文件到服务本地
                    ZipUtil.unzip(in,new File(GlobalContstant.UPLOAD_BASE_PATH +"/"+projectName) , Charset.forName("GBK")); // 解压文件
                    paths.add(GlobalContstant.UPLOAD_BASE_PATH +"/"+projectName);
                    ok.add(projectName);
                } catch (IOException e) {
                    e.printStackTrace();
                    FileUtil.del(GlobalContstant.UPLOAD_BASE_PATH +"/"+projectName); // 异常删除文件
                }
            }
        }
        this.importProject(paths,true,saveType);
        Map<String, List> map = new HashMap<>();
        map.put("ok",ok);
        map.put("no",no);
        return map;
    }

    @Override
    @Transactional
    public Map<String, List> importChain(List<String> projectPaths,Integer saveType) {
        return importProject(projectPaths, false,saveType);
    }

    private Map<String, List>  importProject(List<String> projectPaths,Boolean type,Integer saveType){
        List<String> ok = new ArrayList<>();
        List<String> no = new ArrayList<>();
        projectPaths.forEach(projectPath->{
            String projectName = new File(projectPath).getName();
            // 判断该项目是否导入
            Integer count = new Project().selectCount(new LambdaQueryWrapper<Project>().eq(Project::getProjectName, projectName));
            if(count > 0){
                no.add(projectName); // 该项目已存在
            }else {
                Project project = new Project().setProjectName(projectName).setCreateTime(new Date());
                project.insert();
                // 操作表记录
                new Operation().setTargetId(project.getProjectId().toString()).setTargetName(projectName).setOperationDate(new Date())
                        .setOperationTable(OperationEnum.PROJECT.getValue()).setOperationDescription("导入项目").insert();
                if(saveType == 0){
                    // 单思维导图导入
                    savaJsmindAndFile(type, ok, projectPath, projectName, project);
                }else {
                    // 多思维导图导图
                    File[] jsmindFile = FileUtil.ls(projectPath);
                    for (File file : jsmindFile) {
                        String jsmindPath = file.getAbsolutePath();
                        savaJsmindAndFile(type, ok, jsmindPath, projectName, project);
                    }
                }
            }
        });
        Map<String, List> map = new HashMap<>();
        map.put("ok",ok);
        map.put("no",no);
        return map;
    }

    private void savaJsmindAndFile(Boolean type, List<String> ok, String projectPath, String projectName, Project project) {
        String jsmindName = new File(projectPath).getName();
        // 解析该项目目录 获取每个JsmindDataVo.JsMindData（一个目录一个）
        List<JsmindDataVo.JsMindData> dataList = foreachDir(projectPath,null,new ArrayList<>());
        // 获取根节点
        JsmindDataVo.JsMindData root = dataList.stream().filter(data -> data.getParent() == null).collect(Collectors.toList()).get(0);
        String jsmindId = System.currentTimeMillis()+"_"+ UUID.randomUUID().toString().replace("-","");

        forJsMindDataListSaveFile(root, dataList,project.getProjectId(),type);

        JsmindDataVo jsmindDataVo = new JsmindDataVo();
        jsmindDataVo.setFormat("node_tree");
        JsmindDataVo.Meta meta = new JsmindDataVo.Meta();
        meta.setAuthor("测试用户").setName(projectName).setVersion("0.1");
        jsmindDataVo.setMeta(meta);

        jsmindDataVo.setData(root.getChildren().get(0));
        Jsmind jsmind = new Jsmind();
        jsmind.setId(jsmindId).setProjectId(project.getProjectId()).setMindMapName(jsmindName).setData(jsmindDataVo).insert();
        new Operation().setTargetId(jsmind.getId())
                .setOperationTable(OperationEnum.JSMIND.getValue())
                .setTargetName(jsmind.getMindMapName())
                .setOperationDate(new Date())
                .setOperationDescription("导入思维导图")
                .insert();
        ok.add(projectName);
    }


    private void forJsMindDataListSaveFile( JsmindDataVo.JsMindData curr, List<JsmindDataVo.JsMindData> dataList, Long projectId,Boolean type){
        // 获取改节点下文件
        List<com.ywjs.jcsy.entity.File> fileList = null;
        List<File> files = curr.getFiles();
        if (!CollectionUtil.isEmpty(files)) {
            fileList = files.stream().map(file -> {
                String path = file.getAbsolutePath();
                String name = file.getName();
                String[] split = name.split("\\.");
                String fileType = split[split.length - 1];
                com.ywjs.jcsy.entity.File f = new com.ywjs.jcsy.entity.File();
                f.setFilePath(path).setFileName(name).setFileType(fileType).setIsMount(IsMountEnum.YES).setManagementType(ManagementTypeEnum.OUTER_CHAIN).setUploadTime(new Date());
                if(type){
                    // 获取服务器地址
                    String uri = path.replace(System.getProperty("user.dir")+"\\file\\", "");
//                    String url = serverConfig.getUrl();
//                    String virtualPath = staticAccessPath.replace("**", "");
//                    String accessPath = url + virtualPath + uri;s
                    String accessPath = serverConfig.getAccessPath(uri);
                    f.setAccessPath(accessPath).setManagementType(ManagementTypeEnum.UPLOAD);
                }
                return f;
            }).collect(Collectors.toList());
            // 插入文件
            fileService.saveBatch(fileList);
            List<Long> fileIds = fileList.stream().map(file -> file.getFileId()).collect(Collectors.toList());
            curr.setFileIds(fileIds);
            // 绑定文件与项目的关系
            List<FileProjectRelation> fileProjectRelations = fileList.stream().map(file -> new FileProjectRelation().setFileId(file.getFileId()).setProjectId(projectId)).collect(Collectors.toList());
            fileProjectRelationService.saveBatch(fileProjectRelations);
        }
        // 获取当前节点下子节点
        List<JsmindDataVo.JsMindData> children = dataList.stream().filter(data -> curr.getId().equals(data.getPid())).collect(Collectors.toList());
        if(children.size() > 7){ curr.setExpanded(false); }
        curr.setChildren(children);
        // 递归条件
        if( !CollectionUtil.isEmpty(children) ){
            children.forEach(data->forJsMindDataListSaveFile(data,dataList,projectId,type));
        }
    }

    @Override
    public List<Map<String,Object>> getProjectsAndFileCount() {
        return this.baseMapper.getProjectsAndFileCount();
    }

    @Override
    public PageVo<ProjectVo> listPage(Long pageNo, Long pageSize,ProjectParamVo vo) {
        // 查询条件：时间段、完成度范围、创建人、项目名称模糊
//        Map<String, Object> queryMap = vo.getQuery();
        String projectName = vo.getProjectName();
        LambdaQueryWrapper<Project> projectLambdaQueryWrapper = new LambdaQueryWrapper<>();
        if(!StrUtil.isEmpty(projectName)){
            projectLambdaQueryWrapper.like(Project::getProjectName,projectName);
        }
        String createUser = vo.getCreateUser();
        if(!StrUtil.isEmpty(createUser)){
            projectLambdaQueryWrapper.eq(Project::getCreateUser,createUser);
        }
        Integer startProgressOf = vo.getStartProgressOf();
        Integer endProgressOf = vo.getEndProgressOf();
        if(startProgressOf != null || endProgressOf != null){
            if(startProgressOf == null){
                projectLambdaQueryWrapper.lt(Project::getProgressOf,endProgressOf);
            }else if(endProgressOf == null){
                projectLambdaQueryWrapper.gt(Project::getProgressOf,startProgressOf);
            }else {
                projectLambdaQueryWrapper.between(Project::getProgressOf,startProgressOf,endProgressOf);
            }
        }
        List<Date> startEndTime = vo.getStartEndTime();
        if(!CollectionUtil.isEmpty(startEndTime)){
            projectLambdaQueryWrapper.ge(Project::getStartTime,startEndTime.get(0)).le(Project::getEndTime,startEndTime.get(1));
        }
        IPage<Project> projectIPage = new Project().selectPage(new Page<>(pageNo, pageSize), projectLambdaQueryWrapper.orderByDesc(Project::getCreateTime));
        List<Project> projecs = projectIPage.getRecords();
        // 获取额外信息
        List<ProjectVo> vos = projecs.stream().map(project -> {
            ProjectVo projectVo = new ProjectVo();
            BeanUtil.copyProperties(project, projectVo);
            // 获取改项目下文件数
            Integer fileCount = new FileProjectRelation().selectCount(new LambdaQueryWrapper<FileProjectRelation>().eq(FileProjectRelation::getProjectId, project.getProjectId()));
//            // 获取改项目已挂载文件数量 - 获取该项目下思维导图
//            List<Jsmind> jsminds = new Jsmind().selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getProjectId, project.getProjectId()));
//            // 遍历思维导图获取挂载文件数
//            Set<Long> mountOkFileIds = new HashSet<Long>();
//            if(!CollectionUtil.isEmpty(jsminds)){
//                List<JsmindDataVo.JsMindData> collect = jsminds.stream().map(jsmind -> jsmind.getData().getData()).collect(Collectors.toList());
//                mountOkCount(collect,mountOkFileIds);
//            }
//            // 标签标记文件数
//            Integer tagFileCount = fileService.tagFileCount(project.getProjectId());

            // 获取该项目下思维导图数
            Integer jsmindCount = new Jsmind().selectCount(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getProjectId, project.getProjectId()));
            // 组织起止时间

            Date startTime = project.getStartTime();
            Date endTime = project.getEndTime();
            String startTimeStr = (startTime == null) ? "" : DateUtil.format(startTime, "yyyy-MM-dd");
            String endTimeStr =  (endTime == null) ? "" : DateUtil.format(endTime, "yyyy-MM-dd");
            String startStopTimeStr =  !(startTime == null && endTime == null) ?   startTimeStr + "至" + endTimeStr : "";
//            projectVo.setMountOkCount(mountOkFileIds.size());
//            projectVo.setMountNoCount(fileCount - mountOkFileIds.size());
//            projectVo.setTagFileCount(tagFileCount);
            projectVo.setFileCount(fileCount);
            projectVo.setJsmindCount(jsmindCount);
            projectVo.setStartStopTime(startStopTimeStr);
            projectVo.setKey(projectVo.getProjectId());
            return projectVo;
        }).collect(Collectors.toList());
        Long total = projectIPage.getTotal();
        PageVo<ProjectVo> projectVoPageVo = new PageVo<ProjectVo>().setData(vos).setTotal(total.intValue()).setPageNum(pageNo.intValue()).setPageSize(pageSize.intValue());
        return projectVoPageVo;
    }

    private void mountOkCount(List<JsmindDataVo.JsMindData> JsMindDatas,Set<Long> mountOkFileIds){
        JsMindDatas.forEach(data->{
            List<Long> fileIds = data.getFileIds();
            if(!CollectionUtil.isEmpty(fileIds)){mountOkFileIds.addAll(fileIds);}
            // 递归条件
            if(!CollectionUtil.isEmpty(data.getChildren())){
                mountOkCount(data.getChildren(),mountOkFileIds);
            }
        });
    }

    @Override
    public ProjectBaseInfoVo getProjectBaseInfo(Long projectId) {
        Project project = new Project().setProjectId(projectId).selectById();
        ProjectBaseInfoVo projectBaseInfoVo = new ProjectBaseInfoVo();
        BeanUtil.copyProperties(project,projectBaseInfoVo);
        // 封装额外信息
        // 获取改项目下文件数
        Integer fileCount = new FileProjectRelation().selectCount(new LambdaQueryWrapper<FileProjectRelation>().eq(FileProjectRelation::getProjectId, projectId));
        // 获取改项目已挂载文件数量 - 获取该项目下思维导图
        List<Jsmind> jsminds = new Jsmind().selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getProjectId, projectId));
        // 遍历思维导图获取挂载文件数
        Set<Long> mountOkFileIds = new HashSet<Long>();
        if(!CollectionUtil.isEmpty(jsminds)){
            List<JsmindDataVo.JsMindData> collect = jsminds.stream().map(jsmind -> jsmind.getData().getData()).collect(Collectors.toList());
            mountOkCount(collect,mountOkFileIds);
        }
        // 标签标记文件数
        Integer tagFileCount = fileService.tagFileCount(projectId);

        // 获取该项目下思维导图数
        Integer jsmindCount = new Jsmind().selectCount(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getProjectId, projectId));

        List<Date> dates = new ArrayList<>();
        dates.add(project.getStartTime());
        dates.add(project.getEndTime());
        projectBaseInfoVo.setStartEndTime(dates);
        projectBaseInfoVo.setMountOkCount(mountOkFileIds.size());
        projectBaseInfoVo.setMountNoCount(fileCount - mountOkFileIds.size());
        projectBaseInfoVo.setTagFileCount(tagFileCount);
        projectBaseInfoVo.setFileCount(fileCount);
        projectBaseInfoVo.setJsmindCount(jsmindCount);

        return projectBaseInfoVo;
    }

    @Override
    public List<String> getProjectNames() {
        List<Project> list = this.list(new LambdaQueryWrapper<Project>().select(Project::getProjectName));
        return list.stream().map(project -> project.getProjectName()).collect(Collectors.toList());
    }

    @Override
    public void deleteProjectByIds(List<Integer> ids) {
        // 获取要删除的项目，判断哪些是上传哪些是外链,上传则删除服务路径下管理的文件
        List<Project> projects = this.list(new LambdaQueryWrapper<Project>().in(Project::getProjectId, ids));


        List<FileProjectRelation> fileProjectRelations = new FileProjectRelation().selectList(new LambdaQueryWrapper<FileProjectRelation>().in(FileProjectRelation::getProjectId, ids));
        // 删除项目文件关联表
        fileProjectRelationService.removeByIds(fileProjectRelations.stream().map(item->item.getId()).collect(Collectors.toList()));
        // 删除文件
        List<Long> fileIds = fileProjectRelations.stream().map(item -> item.getFileId()).collect(Collectors.toList());
        fileService.removeByIds(fileIds);
        // 删除项目
        removeByIds(ids);
        // 删除思维导图
        // 查询待删除的思维导图
        Jsmind jsmind = new Jsmind();
        List<Jsmind> jsminds = jsmind.selectList(new LambdaQueryWrapper<Jsmind>().in(Jsmind::getProjectId, ids));
        jsmind.delete(new LambdaQueryWrapper<Jsmind>().in(Jsmind::getProjectId,ids));

        // 操作表
        List<String> projectIds = ids.stream().map(id -> id.toString()).collect(Collectors.toList());
        new Operation().delete(new LambdaQueryWrapper<Operation>().in(Operation::getTargetId,projectIds).eq(Operation::getOperationTable, OperationEnum.PROJECT.getValue()));
        List<String> jsmindIds = jsminds.stream().map(item -> item.getId()).collect(Collectors.toList());
        new Operation().delete(new LambdaQueryWrapper<Operation>().in(Operation::getTargetId,jsmindIds).eq(Operation::getOperationTable, OperationEnum.JSMIND.getValue()));

        collectionService.remove(new LambdaQueryWrapper<CollectionSc>().in(CollectionSc::getTargetId,projectIds).eq(CollectionSc::getCollectionType, CollectionTypeEnum.PROJECT));
        collectionService.remove(new LambdaQueryWrapper<CollectionSc>().in(CollectionSc::getTargetId,jsmindIds).eq(CollectionSc::getCollectionType, CollectionTypeEnum.MIND_MAP));
        collectionService.remove(new LambdaQueryWrapper<CollectionSc>().in(CollectionSc::getTargetId,fileIds).eq(CollectionSc::getCollectionType, CollectionTypeEnum.FILE));
        // 将文件删除放到最后原因，放到前边文件删除了，后边数据库操作失败事务回滚导致删除失败，而实际文件删除问题
        projects.forEach(project -> {
            // 判断服务路径下是否有该项目目录，有则删除
            String projectName = project.getProjectName();
            String projectPath = GlobalContstant.UPLOAD_BASE_PATH + projectName;
            if(FileUtil.exist(projectPath)){
                FileUtil.del(projectPath);
            }
        });
    }

    @Override
    public List<Project3LevelVo> getProject3Level() {
        // 获取所有项目
        List<Project> projects = this.list();
        // 获取思维导图
        List<Long> projectIds = projects.stream().map(Project::getProjectId).collect(Collectors.toList());
        List<Jsmind> jsminds = jsmindMapper.selectList(new LambdaQueryWrapper<Jsmind>().in(Jsmind::getProjectId, projectIds));
        // 获取一级分类
        List<Project3LevelVo> project1LevelVos = projects.stream().map(project -> {
            // 获取该项目下思维导图
            Project3LevelVo project3LevelVo = new Project3LevelVo();
            String projectId = project.getProjectId().toString();
            project3LevelVo.setTitle(project.getProjectName())
                    .setKey("project_"+UUID.randomUUID().toString())
                    .setValue(JSON.toJSONString(new Project3LevelVo.Value(projectId,null,null)));
            return project3LevelVo;
        }).collect(Collectors.toList());

        // 获取2级数据
        List<Project3LevelVo> project2LevelVos = jsminds.stream().map(item -> {
            Project3LevelVo project2LevelVo = new Project3LevelVo();
            project2LevelVo.setTitle(item.getMindMapName())
                    .setKey("jsmind_" + UUID.randomUUID().toString())
                    .setValue(JSON.toJSONString(new Project3LevelVo.Value(item.getProjectId().toString(), item.getId(), null)));
            return project2LevelVo;
        }).collect(Collectors.toList());

        // 获取三级数据
        List<Project3LevelVo> project3LevelVos = new ArrayList<>();
        jsminds.forEach(item->{
            List<JsmindDataVo.JsMindData> nodes = JsmindUtil.getNodes(item);
            // 转化为3级数据
            List<Project3LevelVo> project3LevelVoByItem = nodes.stream().map(node -> new Project3LevelVo()
                    .setTitle(node.getTopic())
                    .setKey("node_" + UUID.randomUUID().toString())
                    .setValue(JSON.toJSONString(new Project3LevelVo.Value(item.getProjectId().toString(), item.getId(), node.getId())))
            ).collect(Collectors.toList());
            project3LevelVos.addAll(project3LevelVoByItem);

        });

        // 2级别数据组装3级数据
        project2LevelVos.forEach(level2->{
            List<Project3LevelVo> collect = project3LevelVos.stream().filter(level3 ->{
                Project3LevelVo.Value value3 = JSON.parseObject(level3.getValue(), Project3LevelVo.Value.class);
                Project3LevelVo.Value value2 = JSON.parseObject(level2.getValue(), Project3LevelVo.Value.class);
                return  value3.getJsmindId().equals(value2.getJsmindId());
            }).collect(Collectors.toList());
            level2.setChildren(collect);
        });

        // 1级数据组装2级数据
        project1LevelVos.forEach(level1->{
            List<Project3LevelVo> collect = project2LevelVos.stream().filter(level2 ->{
                Project3LevelVo.Value value1 = JSON.parseObject(level1.getValue(), Project3LevelVo.Value.class);
                Project3LevelVo.Value value2 = JSON.parseObject(level2.getValue(), Project3LevelVo.Value.class);
//                level2.getValue().getProjectId().equals(level1.getValue().getProjectId());
                return value1.getProjectId().equals(value2.getProjectId());
            }).collect(Collectors.toList());
            level1.setChildren(collect);
        });
        return project1LevelVos;
    }

    @Override
    public Project getProjectByName(String projectName) {
        return this.getOne(new LambdaQueryWrapper<Project>().eq(Project::getProjectName, projectName));
    }


    private List<JsmindDataVo.JsMindData> foreachDir(String path, String  pid, List<JsmindDataVo.JsMindData> dataList){
        File[] ls = FileUtil.ls(path);
        List<File> files = Arrays.asList(ls);
        List<File> filePathList = files.stream().filter(file -> file.isFile()).collect(Collectors.toList());
        List<File> dirs = files.stream().filter(item -> !item.isFile()).collect(Collectors.toList());
        // 获取当前路径名称
        String nodeName = new File(path).getName();
        // 组织数据
        JsmindDataVo.JsMindData jsMindData = new JsmindDataVo.JsMindData();
        jsMindData.setExpanded(true);
        jsMindData.setTopic(nodeName);
        String id = UUID.randomUUID().toString().replaceAll("-","");
        jsMindData.setId(id);
        jsMindData.setPid(pid);
        // 保存该路径下的文件集合
        jsMindData.setFiles(filePathList);
        if(pid == null) jsMindData.setIsroot(true);
        else jsMindData.setIsroot(false);
        // 将数据添加到集合
        dataList.add(jsMindData);

        // 递归条件
        if(dirs.size() > 0){
            dirs.forEach(item->foreachDir(item.getAbsolutePath(),id,dataList));
        }
        return dataList;
    }


}
