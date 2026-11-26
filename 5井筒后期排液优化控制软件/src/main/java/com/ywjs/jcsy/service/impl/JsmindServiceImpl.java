package com.ywjs.jcsy.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.exceptions.UtilException;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.util.ZipUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.ywjs.jcsy.common.constant.DimensionEnum;
import com.ywjs.jcsy.common.constant.GlobalContstant;
import com.ywjs.jcsy.common.enums.CollectionTypeEnum;
import com.ywjs.jcsy.common.enums.ManagementTypeEnum;
import com.ywjs.jcsy.common.enums.OperationEnum;
import com.ywjs.jcsy.config.ServerConfig;
import com.ywjs.jcsy.entity.*;
import com.ywjs.jcsy.factory.EntityFactory;
import com.ywjs.jcsy.mapper.DimensionMapper;
import com.ywjs.jcsy.mapper.JsmindMapper;
import com.ywjs.jcsy.mapper.ProjectMapper;
import com.ywjs.jcsy.service.FileProjectRelationService;
import com.ywjs.jcsy.service.FileService;
import com.ywjs.jcsy.service.JsmindService;
import com.ywjs.jcsy.service.ProjectService;
import com.ywjs.jcsy.utils.*;
import com.ywjs.jcsy.vo.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.Charset;
import java.util.*;
import java.util.stream.Collectors;

/**
 * <p>
 * 服务实现类
 * </p>
 *
 * @author ${author}
 * @since 2020-08-21
 */
@Service
@Transactional
public class JsmindServiceImpl extends ServiceImpl<JsmindMapper, Jsmind> implements JsmindService {

    @Value("${jcsy.character}")
    String character;
    @Autowired
    ProjectService projectService;
    @Autowired
    FileService fileService;
    @Autowired
    ProjectMapper projectMapper;
    @Autowired
    DimensionMapper dimensionMapper;
    @Autowired
    FileProjectRelationService fileProjectRelationService;
    @Autowired
    ServerConfig serverConfig;

    @Override
    public Jsmind generateJsmind(List<String> jsmindIds, String dimensionName) {
        List<Jsmind> jsminds = this.listByIds(jsmindIds);
        if (CollectionUtil.isEmpty(jsminds)) {
            jsminds = this.list(Wrappers.<Jsmind>lambdaQuery().eq(Jsmind::getIsTemplate, false));
        }
        jsminds.forEach(item-> JsmindUtil.setNodeLevel(item.getData().getData(),1) );
        JsmindUtil.setDateFileMapping(jsminds);

        Jsmind jsmind = null;
        if (DimensionEnum.EVENT.getValue().equals(dimensionName)) {
            jsmind = eventDimension(jsminds);
        }
        if (DimensionEnum.TIME.getValue().equals(dimensionName)) {
            jsmind = dateDimension(jsminds);
        }
        if (DimensionEnum.TYPE.getValue().equals(dimensionName)) {
            jsmind = typeDimension(jsminds);
        }
        // 思维导图节点排序
        JsmindUtil.nodeSort(jsmind.getData().getData());
        JsmindUtil.setNodeLevel(jsmind.getData().getData(),1);
        JsmindUtil.setExpanded(jsmind.getData().getData(),4,false);
        // 重新生成节点id
        return jsmind;
    }




    @Override
    public Jsmind getJsmindBydimensionId(Long dimensionId) {
        JsmindDataVo.JsMindData rootjsMindData = JsmindUtil.getRootNode("项目库");

        List<Jsmind> jsminds = dimensionId == -1 ?
                new Jsmind().selectList(new LambdaQueryWrapper<Jsmind>().isNull(Jsmind::getDimensionId).eq(Jsmind::getIsTemplate, 0).isNotNull(Jsmind::getProjectId)) :
                new Jsmind().selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getDimensionId, dimensionId).eq(Jsmind::getIsTemplate, 0).isNotNull(Jsmind::getProjectId));
        // 根据项目id分组
        Map<Long, List<Jsmind>> groupProjectIdMap = jsminds.stream().collect(Collectors.groupingBy(Jsmind::getProjectId));
        List<JsmindDataVo.JsMindData> projectNode = new ArrayList<>();

        // 获取项目名称
        List<Project> projects = projectService.listByIds(groupProjectIdMap.keySet());

        for (Map.Entry<Long, List<Jsmind>> entry : groupProjectIdMap.entrySet()) {
            Long projectId = entry.getKey();
            List<Project> projectsList = projects.stream().filter(a -> a.getProjectId().equals(projectId)).collect(Collectors.toList());
            if(!CollectionUtil.isEmpty(projectsList)){
                // 获取项目名称
                Project project = projectsList.get(0);
                // 组织项目名节点
                JsmindDataVo.JsMindData jsMindData = new JsmindDataVo.JsMindData();
                jsMindData.setId(UUID.randomUUID().toString());
                jsMindData.setTopic(project.getProjectName());
                jsMindData.setExpanded(true);
                jsMindData.setIsroot(false);
                // 该项目下思维导图
                List<Jsmind> jsmindsByProjectId = entry.getValue();
                if (!CollectionUtil.isEmpty(jsmindsByProjectId)) {
                    // 将该数据添加到项目节点上
                    List<JsmindDataVo.JsMindData> jsmindNode = jsmindsByProjectId.stream().map(item -> {
                        JsmindDataVo.JsMindData data = item.getData().getData();
                        data.setIsroot(false);
                        // 收起所有节点
                        JsmindUtil.packUpAllNode(data);
                        return data;
                    }).collect(Collectors.toList());
                    jsMindData.setChildren(jsmindNode);
                }
                projectNode.add(jsMindData);
            }

        }
        rootjsMindData.setChildren(projectNode);

        // 重新生成各个节点id
        JsmindUtil.generateNodeId(rootjsMindData);
        Jsmind jsmind = JsmindUtil.getJsmindByRootNode(rootjsMindData);

        return jsmind;
    }

    @Override
    public List<CountVo> countNodeFile(Long projectId) {
        LambdaQueryWrapper<Jsmind> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(Jsmind::getProjectId, projectId);
        }
        List<Jsmind> jsminds = new Jsmind().selectList(wrapper);
        // 统计节点及其文件数量
        List<CountVo> countNodeFileList = new ArrayList<>();
        jsminds.forEach(jsmind -> {
            JsmindDataVo.JsMindData data = jsmind.getData().getData();
            List<JsmindDataVo.JsMindData> JsMindDatas = new ArrayList<>();
            JsMindDatas.add(data);
            JsmindUtil.countNodeFile(JsMindDatas, countNodeFileList);
        });
        List<CountVo> dataList = new ArrayList<>();
        Map<String, List<CountVo>> groupByMap = countNodeFileList.stream().collect(Collectors.groupingBy(CountVo::getName));
        for (Map.Entry<String, List<CountVo>> entry : groupByMap.entrySet()) {
            int sum = entry.getValue().stream().mapToInt(CountVo::getValue).sum();
            CountVo countVo = new CountVo().setName(entry.getKey()).setValue(sum);
            dataList.add(countVo);
        }
        dataList.sort((o1, o2) -> o2.getValue() - o1.getValue());
        int toIndex = dataList.size() > 300 ? 300 : dataList.size();
        return dataList.subList(0, toIndex);
    }

    @Override
    public Object countTag(Long projectId) {
        // 获取所有思维导图
        List<Jsmind> jsminds = this.baseMapper.selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getProjectId, projectId));
        List<JsmindTagVo> jsmindTagVos = new ArrayList<>();
        jsminds.forEach(jsmind -> {
            // 获取改思维导图下文件id集合
            List<Long> fileIds = new ArrayList<>();
            JsmindDataVo.JsMindData data = jsmind.getData().getData();
            List<JsmindDataVo.JsMindData> jsMindDataArrayList = new ArrayList<>();
            jsMindDataArrayList.add(data);
            JsmindUtil.getFileIds(data, fileIds);
            List<FileTagRelation> fileTagRelations = new FileTagRelation().selectList(new LambdaQueryWrapper<FileTagRelation>().in(FileTagRelation::getFileId, fileIds));
            // 根据标签id分组
            Map<Long, List<FileTagRelation>> groupBytagIdMap = fileTagRelations.stream().collect(Collectors.groupingBy(FileTagRelation::getTagId));
            if (!CollectionUtil.isEmpty(groupBytagIdMap)) {
                for (Map.Entry<Long, List<FileTagRelation>> entry : groupBytagIdMap.entrySet()) {
                    Long tagId = entry.getKey();
                    // 获取标签名称
                    Tag tag = new Tag().setTagId(tagId).selectById();
                    JsmindTagVo jsmindTagVo = new JsmindTagVo();
                    jsmindTagVo.setTagName(tag.getTagName());
                    jsmindTagVo.setCount(entry.getValue().size());
                    jsmindTagVo.setMindMapName(jsmind.getMindMapName());
                    jsmindTagVos.add(jsmindTagVo);
                }
            }
        });
        // 获取所有标签
        List<String> tagNames = jsmindTagVos.stream().map(item -> item.getTagName()).distinct().collect(Collectors.toList());

        List<Object> list = new ArrayList<>();
        // 根据思维导图分组
        Map<String, List<JsmindTagVo>> groupMindMapMap = jsmindTagVos.stream().collect(Collectors.groupingBy(JsmindTagVo::getMindMapName));
        for (Map.Entry<String, List<JsmindTagVo>> entry : groupMindMapMap.entrySet()) {
            Map<String, Object> map = new HashMap<>();
            map.put("name", entry.getKey());
            // 获取每个标签下的文件，无则为null
            List<Object> data = new ArrayList<>();
            tagNames.forEach(tagName -> {
                List<JsmindTagVo> vos = entry.getValue();
                List<Integer> collect = vos.stream().filter(item -> item.getTagName().equals(tagName)).map(item -> item.getCount()).collect(Collectors.toList());
                if (!CollectionUtil.isEmpty(collect)) {
                    data.add(collect.get(0));
                } else {
                    data.add(null);
                }
            });
            map.put("data", data);
            list.add(map);
        }
        Map<String, Object> result = new HashMap<>();
        result.put("tagNames", tagNames);
        result.put("data", list);
        result.put("mindMaps", null);
        return result;
    }

    @Override
    public void uninstallFile(String jsmindId, String nodeId, Long fileId) {
        // 获取思维导图
        Jsmind jsmind = this.baseMapper.selectOne(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getId, jsmindId));
        // 获取目标文件节点卸载文件
        JsmindDataVo.JsMindData data = jsmind.getData().getData();
        JsmindUtil.uninstallFileByFileIdByNodeId(data, nodeId, fileId);
        // 更新数据库
        jsmind.updateById();
        File file = new File().setFileId(fileId).selectById();
        file.updateById();
        Operation operation = EntityFactory.create(Operation.class).setOperationDate(new Date()).setTargetId(jsmind.getId()).setOperationDate(new Date())
                .setOperationTable(OperationEnum.JSMIND.getValue()).setTargetName(jsmind.getMindMapName()).setOperationDescription("更新思维导图");
        operation.update(new LambdaQueryWrapper<Operation>().eq(Operation::getTargetId, jsmind.getId()).eq(Operation::getOperationTable, OperationEnum.JSMIND.getValue()));
    }

    @Override
    public void saveOrUpdateMindMap(Jsmind jsmind) {
        // 获取数据库思维导图数据
        Jsmind jsminddb = this.baseMapper.selectOne(Wrappers.<Jsmind>lambdaQuery().eq(Jsmind::getId, jsmind.getId()).eq(Jsmind::getIsTemplate, false));
        Operation operation = EntityFactory.create(Operation.class).setOperationDate(new Date()).setTargetName(jsmind.getMindMapName());
        // TODO 将思维导图中的文件与项目关联：查询当前思维导图
        if (jsminddb == null) {
            // 数据库没有新添
            jsmind.insert();
            operation.setOperationDescription("新增思维导图").setTargetId(jsmind.getId()).setTargetName(jsmind.getMindMapName()).setOperationTable(OperationEnum.JSMIND.getValue());
            operation.insert();
        } else {
            jsmind.updateById();
            operation.setOperationDescription("更新思维导图");
            operation.update(new LambdaQueryWrapper<Operation>().eq(Operation::getTargetId, jsmind.getId()).eq(Operation::getOperationTable, OperationEnum.JSMIND.getValue()));

            Long projectId = jsmind.getProjectId();
            if (projectId != null) {
                // 将当前当前思维导图文件与项目关联
                Jsmind dbJsmind = this.getById(jsmind.getId());
                List<Long> fileIds = JsmindUtil.getFileIds(dbJsmind.getData().getData(), new ArrayList<Long>());
                // 查看哪些文件与当前思维导图已经关联
                List<FileProjectRelation> existFileProjects = fileProjectRelationService.list(new LambdaQueryWrapper<FileProjectRelation>().eq(FileProjectRelation::getProjectId, projectId).in(FileProjectRelation::getFileId, fileIds));
                // 过滤已经存在的文件id集合，得到需要跟当前项目关联的文件
                if (!CollectionUtil.isEmpty(existFileProjects)) {
                    List<Long> existFiles = existFileProjects.stream().map(FileProjectRelation::getFileId).collect(Collectors.toList());
                    fileIds = fileIds.stream().filter(item -> !existFiles.contains(item)).collect(Collectors.toList());
                }
                if (!CollectionUtil.isEmpty(fileIds)) {
                    List<FileProjectRelation> fileProjectRelations = fileIds.stream().map(fileId -> new FileProjectRelation().setFileId(fileId).setProjectId(projectId)).collect(Collectors.toList());
                    fileProjectRelationService.saveBatch(fileProjectRelations);
                }
            }
        }
    }

    @Override
    public Jsmind add(Jsmind jsmind) {
        jsmind.setId(null).setUpdateTime(null).setCreateTime(null).setCreateUser(null).setUpdateUser(null);
        jsmind.insert();
        EntityFactory.create(Operation.class).setTargetId(jsmind.getId())
                .setOperationTable(OperationEnum.JSMIND.getValue()).setTargetName(jsmind.getMindMapName())
                .setOperationDate(new Date()).setOperationDescription("新建思维导图").insert();
        return jsmind;
    }

    @Override
    public void deleteById(String id) {
        EntityFactory.create(Jsmind.class).setId(id).deleteById();
        EntityFactory.create(Operation.class).delete(new LambdaQueryWrapper<Operation>().eq(Operation::getTargetId, id).eq(Operation::getOperationTable, OperationEnum.JSMIND.getValue()));
        EntityFactory.create(CollectionSc.class).delete(new LambdaQueryWrapper<CollectionSc>().eq(CollectionSc::getTargetId, id).eq(CollectionSc::getCollectionType, CollectionTypeEnum.MIND_MAP));
    }

    @Override
    public void saveMindMapTotemplate(Jsmind jsmind, String name) {
        jsmind.setId(null).setIsTemplate(true).setMindMapName(name);
        jsmind.getData().getMeta().setName(name);
        // 去除挂载文件和topic
        JsmindUtil.unMountAndChangeTopic(jsmind.getData().getData());
        jsmind.insert();
        jsmind.setIsTemplate(true);
        jsmind.updateById();
    }

    @Override
    public Boolean checkMindMapName(String name) {
        Integer count = this.baseMapper.selectCount(new LambdaQueryWrapper<Jsmind>().in(Jsmind::getMindMapName, name));
        return (count > 0) ? true : false;
    }

    @Override
    public List<JsmindDataVo.JsMindData> getNodesByProjectId(Long projectId) {
        LambdaQueryWrapper<Jsmind> wrapper = new LambdaQueryWrapper<>();
        if (projectId != null) {
            wrapper.eq(Jsmind::getProjectId, projectId);
        }
        // 获取思维导图
        List<Jsmind> jsminds = this.list(wrapper);
        return null;
    }

    @Override
    public AssociatedVo getAssociated(String jsmindId) {
        AssociatedVo associatedVo = new AssociatedVo();
        // 查询所有项目
        List<Project> projects = projectMapper.selectList(null);
        List<AssociatedVo.ProjectVo> projectVos = projects.stream().map(item -> {
            AssociatedVo.ProjectVo projectVo = new AssociatedVo.ProjectVo();
            projectVo.setProjectId(item.getProjectId());
            projectVo.setProjectName(item.getProjectName());
            return projectVo;
        }).collect(Collectors.toList());
        List<Dimension> dimensions = dimensionMapper.selectList(null);
        associatedVo.setDimensions(dimensions);
        associatedVo.setProjects(projectVos);
        if (jsmindId == null) {
            // 查询所有
            List<Jsmind> jsminds = list();
            if (!CollectionUtil.isEmpty(jsminds)) {
                List<AssociatedVo.JsmindAssociatedVo> jsmindAssociatedVos = new ArrayList<>();
                for (Jsmind jsmind : jsminds) {
                    AssociatedVo.JsmindAssociatedVo jsmindAssociatedVos1 = getJsmindAssociatedVos(projects, dimensions, jsmind);
                    jsmindAssociatedVos.add(jsmindAssociatedVos1);
                }
                associatedVo.setJsmindAssociatedVos(jsmindAssociatedVos);
            }
        } else {
            // 查询当前
            Jsmind jsmind = this.baseMapper.selectById(jsmindId);
            if (jsmind != null) {
                AssociatedVo.JsmindAssociatedVo jsmindAssociatedVos1 = getJsmindAssociatedVos(projects, dimensions, jsmind);
                associatedVo.setJsmindAssociatedVos(Arrays.asList(jsmindAssociatedVos1));
            }
        }
        return associatedVo;
    }

    @Override
    public void associated(AssociatedIdVo associatedId) {
        Long dimensionId = associatedId.getDimensionId();
        String jsmindId = associatedId.getJsmindId();
        Long projectId = associatedId.getProjectId();
        Jsmind jsmind = new Jsmind();
        jsmind.setId(jsmindId).setDimensionId(dimensionId).setProjectId(projectId);
        this.updateById(jsmind);
        // 将当前当前思维导图文件与项目关联
        Jsmind dbJsmind = this.getById(jsmindId);
        List<Long> fileIds = JsmindUtil.getFileIds(dbJsmind.getData().getData(), new ArrayList<Long>());
        // 查看哪些文件与当前思维导图已经关联
        List<FileProjectRelation> existFileProjects = fileProjectRelationService.list(new LambdaQueryWrapper<FileProjectRelation>().eq(FileProjectRelation::getProjectId, projectId).in(FileProjectRelation::getFileId, fileIds));
        // 过滤已经存在的文件id集合，得到需要跟当前项目关联的文件

        if (!CollectionUtil.isEmpty(existFileProjects)) {
            List<Long> existFiles = existFileProjects.stream().map(FileProjectRelation::getFileId).collect(Collectors.toList());
            fileIds = fileIds.stream().filter(item -> !existFiles.contains(item)).collect(Collectors.toList());

        }
        if (!CollectionUtil.isEmpty(fileIds)) {
            List<FileProjectRelation> fileProjectRelations = fileIds.stream().map(fileId -> new FileProjectRelation().setFileId(fileId).setProjectId(projectId)).collect(Collectors.toList());
            fileProjectRelationService.saveBatch(fileProjectRelations);
        }
    }

    @Override
    public JsmindDataVo.JsMindData getCopyNodeByNodeId(Jsmind jsmind, String nodeId) {
        JsmindDataVo.JsMindData data = jsmind.getData().getData();
        List<JsmindDataVo.JsMindData> copyNodeByNodeId = JsmindUtil.getNodeByNodeId(data, nodeId, new ArrayList<JsmindDataVo.JsMindData>());
        JsmindDataVo.JsMindData jsMindData = copyNodeByNodeId.get(0);
        // 修改节点id
        JsmindUtil.generateNodeId(jsMindData);
        return jsMindData;
    }

    @Override
    public Jsmind pasteNode(PasteNodeVo vo) {
        Jsmind jsmind = vo.getJsmind();
        JsmindDataVo.JsMindData data = jsmind.getData().getData();
        JsmindDataVo.JsMindData copyNode = vo.getCopyNode();
        // 修改节点id
        JsmindUtil.generateNodeId(copyNode);
        String copyNodeId = vo.getCopyNodeId();
        // 获取要拷贝到的节点
        List<JsmindDataVo.JsMindData> copyByNodeBy = JsmindUtil.getNodeByNodeId(data, copyNodeId, new ArrayList<JsmindDataVo.JsMindData>());
        JsmindDataVo.JsMindData copyToNode = copyByNodeBy.get(0);
        List<JsmindDataVo.JsMindData> children = copyToNode.getChildren();
        // 修改拷贝节点的左右方向
//        String direction = copyToNode.getDirection();
//        JsmindUtil.changeDirection(copyNode,direction);
        children.add(copyNode);
        return jsmind;
    }

    @Override
    public void exportJsmind(Jsmind jsmind, HttpServletResponse response) throws Exception {
        // 获取根节点名称
        String rootName = jsmind.getData().getData().getTopic();
        rootName = rootName.substring(rootName.lastIndexOf(">") + 1, rootName.length()).replaceAll("[(]{1}[0-9]+[)]", "");
        // 输出思维导图的json格式，jm文件
        String bashPath = GlobalContstant.TMP_PATH + UUID.randomUUID().toString().replace("-", "") + java.io.File.separator;
        String baseFilePath = bashPath + rootName;
        FileUtil.mkdir(bashPath);
        String jsmindFilePaht = bashPath + rootName + ".jm";
        IoUtil.write(new FileOutputStream(jsmindFilePaht), true, JSON.toJSONString(jsmind).getBytes());

        JsmindDataVo.JsMindData data = jsmind.getData().getData();
        Map<String, List<String>> nodeIdByFileIds = JsmindUtil.copyFileByJsmind(data, bashPath, fileService, new HashMap<String, List<String>>());

        String nodeIdByFileIdsPath = bashPath + rootName + ".conf";
        IoUtil.write(new FileOutputStream(nodeIdByFileIdsPath), true, JSON.toJSONString(nodeIdByFileIds).getBytes());

        // 压缩临时文件
        String zipPath = bashPath + rootName + ".zip";
        ZipUtil.zip(baseFilePath, zipPath, true);

        // 将压缩包和jm文件打包
        String mergePath = bashPath + "merge";
        FileUtil.mkdir(mergePath);
        FileUtil.copy(zipPath, mergePath, false);
        FileUtil.copy(jsmindFilePaht, mergePath, false);
        FileUtil.copy(nodeIdByFileIdsPath, mergePath, false);
        String mergeZip = mergePath + ".zip";
        ZipUtil.zip(mergePath, mergeZip, true);

        // 浏览器下载
        FileDownloadUtil.fileDownload(response, mergeZip, "项目压缩包.zip", bashPath);
    }

    @Override
    public Jsmind importNode(MultipartFile files, String nodeId, Jsmind jsmind) {
        Jsmind inportNode = importUpload(files);
        // 获取导入节点
        JsmindDataVo.JsMindData data = inportNode.getData().getData();
        // 查询当前待插入节点
        JsmindDataVo.JsMindData rootData = jsmind.getData().getData();
        List<JsmindDataVo.JsMindData> nodeByNodeId = JsmindUtil.getNodeByNodeId(rootData, nodeId, new ArrayList<>());
        JsmindDataVo.JsMindData jsMindData = nodeByNodeId.get(0);
        List<JsmindDataVo.JsMindData> children = jsMindData.getChildren();
        if (!CollectionUtil.isEmpty(children)) {
            children.add(data);
        } else {
            List<JsmindDataVo.JsMindData> list = new ArrayList<>();
            list.add(data);
            jsMindData.setChildren(list);
        }
        // 重新生成思维导图各节点id
        JsmindUtil.generateNodeId(rootData);
        // 保存思维导图
        jsmind.updateById();
        return jsmind;
    }


    @Override
    public Jsmind importUpload(MultipartFile file) {
        try {
            String bashPath = GlobalContstant.TMP_PATH + UUID.randomUUID().toString().replace("-", "");
            FileUtil.mkdir(bashPath);

            // 将文件保存到指定路径的功能
            String zipPaht = bashPath + java.io.File.separator + file.getOriginalFilename();
            FileOutputStream out = new FileOutputStream(zipPaht);
            IoUtil.write(out, true, file.getBytes());
            try {
                ZipUtil.unzip(zipPaht, Charset.forName("gbk"));
            } catch (UtilException e) {
                ZipUtil.unzip(zipPaht, Charset.forName("utf-8"));
            }
            String zipName = file.getOriginalFilename();
            zipName = zipName.substring(0, zipName.lastIndexOf("."));
            String mergePath = bashPath + java.io.File.separator + zipName + java.io.File.separator + "merge";
            java.io.File[] ls = FileUtil.ls(mergePath);

            Jsmind jsmind = null;
            Map<String, List<String>> confMap = null;

            for (java.io.File l : ls) {
                String name = l.getName();
                if (name.endsWith(".jm")) {
                    FileInputStream in = new FileInputStream(l);
                    // TODO 开发环境与部署环境字符集区别
                    String json = IoUtil.read(in, character);
                    jsmind = JSON.parseObject(json, Jsmind.class);
                    in.close();
                }
                if (name.endsWith(".conf")) {
                    FileInputStream in = new FileInputStream(l);
                    String json = IoUtil.read(in, character);
                    confMap = JSON.parseObject(json, new TypeReference<Map<String, List<String>>>() {
                    });
                    in.close();
                }
            }
            for (java.io.File l : ls) {
                if (l.getName().endsWith(".zip")) {
                    try {
                        ZipUtil.unzip(l.getPath(), Charset.forName("gbk"));
                    } catch (UtilException e) {
                        ZipUtil.unzip(l.getPath(), Charset.forName("utf-8"));
                    }
                    java.io.File exportFile = Arrays.asList(FileUtil.ls(mergePath)).stream().filter(item -> !item.isFile()).collect(Collectors.toList()).get(0);
                    // 获取导入的所有文件绝对路径
                    List<java.io.File> exportFiles = FileUtils.getFiles(exportFile.getPath(), new ArrayList<>());
                    // 将文件移动到服务路径下
                    String newPath = GlobalContstant.UPLOAD_PATH + UUID.randomUUID().toString().replaceAll("-", "") + java.io.File.separator;
                    FileUtil.mkdir(newPath);
                    exportFiles.forEach(item -> FileUtil.move(item, new java.io.File(newPath), false));
                    // 获取上传文件的服务路径
                    List<String> exportFilePaths = exportFiles.stream().map(item -> newPath + item.getName()).collect(Collectors.toList());
                    JsmindDataVo.JsMindData node = jsmind.getData().getData();
                    // 组织数据
                    Long projectId = jsmind.getProjectId();
                    export(node, confMap, exportFilePaths,projectId);
                    String mindMap = jsmind.getMindMapName() + "-" + System.currentTimeMillis();
                    jsmind.setId(null).setMindMapName(mindMap);
                    FileUtil.del(new java.io.File(bashPath)); // 关闭流，否则删除不了
                }
            }
            return jsmind;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    private void export(JsmindDataVo.JsMindData node, Map<String, List<String>> confMap, List<String> exportFilePaths,Long projectId) {
        String nodeId = node.getId();
        // 获取当前节点对应的文件
        List<String> fileNames = confMap.get(nodeId);
        if (!CollectionUtil.isEmpty(fileNames)) {
            // 获取服务路径下对应的文件
            List<File> insetFiles = exportFilePaths.stream().filter(exportFilePath -> { // 过滤服务路径下文件
                java.io.File file = new java.io.File(exportFilePath);
                return fileNames.contains(file.getName());
            }).map(item -> {
                java.io.File file = new java.io.File(item);
                String name = file.getName();
                String realName = name.substring(name.lastIndexOf("_") + 1, name.length());
                String fileType = name.substring(name.lastIndexOf(".") + 1, name.length());
                String newName = UUID.randomUUID().toString().replace("-", "") + "." + fileType;

                String newPath = file.getParent() + java.io.File.separator + newName;
                file.renameTo(new java.io.File(newPath));
                // 获取服务器地址

                String childPath = FileHandleUtil.getChildPath(newPath, GlobalContstant.UPLOAD_BASE_PATH);
                String accessPath = serverConfig.getAccessPath(childPath);
                return new File().setFilePath(newPath).setFileName(realName).setManagementType(ManagementTypeEnum.UPLOAD).setFileType(fileType).setAccessPath(accessPath);
            }).collect(Collectors.toList());
            fileService.saveBatch(insetFiles);
            if(projectId != null){
                List<FileProjectRelation> fileProjectRelations = insetFiles.stream().map(file -> new FileProjectRelation().setProjectId(projectId).setFileId(file.getFileId())).collect(Collectors.toList());
                fileProjectRelationService.saveBatch(fileProjectRelations);
            }
            // 将插入的文件id设置进node
            List<Long> fileIds = insetFiles.stream().map(File::getFileId).collect(Collectors.toList());
            node.setFileIds(fileIds);
        }

        List<JsmindDataVo.JsMindData> children = node.getChildren();
        if (!CollectionUtil.isEmpty(children)) {
            children.forEach(itemNode -> export(itemNode, confMap, exportFilePaths,projectId));
        }
    }

    private AssociatedVo.JsmindAssociatedVo getJsmindAssociatedVos(List<Project> projects, List<Dimension> dimensions, Jsmind jsmind) {
        AssociatedVo.JsmindAssociatedVo jsmindAssociatedVo = new AssociatedVo.JsmindAssociatedVo();
        AssociatedVo.JsmindVo jsmindVo = new AssociatedVo.JsmindVo();
        BeanUtil.copyProperties(jsmind, jsmindVo);
        // 获取当前思维导图关联项目和
        jsmindAssociatedVo.setJsmind(jsmindVo);
        List<Project> projectFilter = projects.stream().filter(item -> item.getProjectId().equals(jsmind.getProjectId())).collect(Collectors.toList());
        if (!CollectionUtil.isEmpty(projectFilter)) {
            Project project = projectFilter.get(0);
            AssociatedVo.ProjectVo projectVo = new AssociatedVo.ProjectVo();
            BeanUtil.copyProperties(project, projectVo);
            jsmindAssociatedVo.setProject(projectVo);
        }
        List<Dimension> dimensionFilter = dimensions.stream().filter(item -> item.getDimensionId().equals(jsmind.getDimensionId())).collect(Collectors.toList());
        if (!CollectionUtil.isEmpty(dimensionFilter)) {
            Dimension dimension = dimensionFilter.get(0);
            AssociatedVo.DimensionVo dimensionVo = new AssociatedVo.DimensionVo();
            BeanUtil.copyProperties(dimension, dimensionVo);
            jsmindAssociatedVo.setDimension(dimensionVo);
        }
        return jsmindAssociatedVo;
    }



    /**
     * 类型维度
     * @param jsminds
     * @return
     */
    private Jsmind typeDimension(List<Jsmind> jsminds) {
        // 修改所有思维导图中心节点名称
        jsminds.forEach(jsmind ->   jsmind.getData().getData().setTopic("类型维度") );
        // 打散合并思维导图
        List<JsmindDataVo.JsMindData> mergeNodes = JsmindUtil.mergeNodesByTypeDimension(jsminds);


        List<JsmindDataVo.JsMindData> resultNodes = new ArrayList<>();
        // 获取二级节点
        List<JsmindDataVo.JsMindData> node2s = mergeNodes.stream().filter(item -> item.getLevel() == 2).collect(Collectors.toList());
        // 获取根节点
        JsmindDataVo.JsMindData rootNode = mergeNodes.stream().filter(node -> node.getLevel() == 1).collect(Collectors.toList()).get(0);
        String PTOpicNode1 = rootNode.getTopic() +"|" +rootNode.getLevel();
        // 获取二级节点下的文件
        node2s.forEach(node2->{
            node2.setPTopic(PTOpicNode1);
            String PTopic = node2.getTopic() + "|" + node2.getLevel();
            List<JsmindDataVo.JsMindData> node2Children = mergeNodes.stream().filter(item ->
                item.getPTopic() != null && item.getPTopic().equals(PTopic)
            ).collect(Collectors.toList());
            if(!CollectionUtil.isEmpty(node2Children)){
                List<Long> fileIds = node2Children.stream().filter(item -> !CollectionUtil.isEmpty(item.getFileIds())).map(item -> item.getFileIds()).flatMap(a -> a.stream()).collect(Collectors.toList());
                if(!CollectionUtil.isEmpty(fileIds)){
                    List<File> files = fileService.listByIds(fileIds);
                    // 根据文件类型分类
                    Map<String, List<File>> groupByFileType = files.stream().collect(Collectors.groupingBy(File::getFileType));
                    // 生成类型节点
                    for (Map.Entry<String, List<File>> entry : groupByFileType.entrySet()) {
                        String fileType = entry.getKey();
                        List<Long> fileIdsByFileType = entry.getValue().stream().map(item -> item.getFileId()).distinct().collect(Collectors.toList());
                        JsmindDataVo.JsMindData node3 = new JsmindDataVo.JsMindData();
                        node3.setTopic(fileType);
                        node3.setFileIds(fileIdsByFileType);
                        node3.setPTopic(PTopic);
                        resultNodes.add(node3);
                    }
                }
            }
        });
        resultNodes.addAll(node2s);


        // 组织成思维导图
        JsmindUtil.generateJsmind(rootNode,resultNodes);
        Jsmind jsmind = JsmindUtil.getJsmindByRootNode(rootNode).setMindMapName("类型维度");
        return jsmind;
    }

    /**
     * 事件维度生成思维导图
     * @param jsminds
     * @return
     */
    private Jsmind eventDimension(List<Jsmind> jsminds) {
        // 修改所有思维导图中心节点名称
        jsminds.forEach(jsmind ->   jsmind.getData().getData().setTopic("事件维度") );
        // 打散合并思维导图
        List<JsmindDataVo.JsMindData> mergeNodes = JsmindUtil.mergeNodes(jsminds);
        List<JsmindDataVo.JsMindData> resultNodes = new ArrayList<>();
        // 生成时间节点，规则有mapping并且有文件
        mergeNodes.forEach(node->{
            List<JsmindDataVo.JsMindData> dateNodes = getDateNodesByEventDimension(node);
            resultNodes.addAll(dateNodes);
        });
        resultNodes.addAll(mergeNodes);

        // 获取根节点
        JsmindDataVo.JsMindData rootNode = resultNodes.stream().filter(node -> node.getLevel() == 1).collect(Collectors.toList()).get(0);
        // 过滤根节点
        List<JsmindDataVo.JsMindData> filterRoot = resultNodes.stream().filter(node -> node.getLevel() != 1).collect(Collectors.toList());
        // 组织成思维导图
        JsmindUtil.generateJsmind(rootNode,filterRoot);
        Jsmind jsmind = JsmindUtil.getJsmindByRootNode(rootNode).setMindMapName("事件维度");
        return jsmind;
    }

    private List<JsmindDataVo.JsMindData> getDateNodesByEventDimension( JsmindDataVo.JsMindData node) {
        List<JsmindDataVo.DateFileMapping> dateFileMappings = node.getDateFileMapping();
        // 时间重复合并文件
        Map<String, Set<Long>> dateFileMap = new HashMap<>();
        if(!CollectionUtil.isEmpty(dateFileMappings)){
            dateFileMappings.forEach(dateFileMapping -> {
                Set<String> dates = dateFileMapping.getDates();
                List<Long> fileIds = dateFileMapping.getFileIds();
                if(!CollectionUtil.isEmpty(fileIds)){
                    // 初始化key
                    dates.forEach(date-> dateFileMap.put(date,new HashSet<>()));
                    // 填充v
                    dates.forEach(date-> {
                        Set<Long> fileIdsSet = dateFileMap.get(date);
                        fileIdsSet.addAll(fileIds);
                    });
                }
            });
        }

        // 生成时间节点
        List<JsmindDataVo.JsMindData> dateNodes = new ArrayList<>();
        for (Map.Entry<String, Set<Long>> entry : dateFileMap.entrySet()) {
            String date = entry.getKey();
            Set<Long> fileIds = entry.getValue();
            JsmindDataVo.JsMindData newNode = new JsmindDataVo.JsMindData();
            newNode.setTopic(date);
            newNode.setPTopic(node.getTopic()+"|"+node.getLevel());
            newNode.setFileIds(new ArrayList<>(fileIds));
            newNode.setLevel(node.getLevel() + 1);
            dateNodes.add(newNode);
        }

        return dateNodes;
    }


    /**
     * 时间维度生成思维导图
     *
     * @param jsminds
     * @return
     */
    private Jsmind dateDimension(List<Jsmind> jsminds) {
        // 修改所有思维导图中心节点名称
        jsminds.forEach(jsmind ->   jsmind.getData().getData().setTopic("时间维度") );
        // 打散合并思维导图
        List<JsmindDataVo.JsMindData> mergeNodes = JsmindUtil.mergeNodes(jsminds);
        // 获取大于二级节点的节点
        List<JsmindDataVo.JsMindData> resultNodes = mergeNodes.stream().filter(item -> item.getLevel() > 2).map(item -> {
            item.setLevel( item.getLevel() + 1);
            String[] split = item.getPTopic().split("\\|");
            item.setPTopic(split[0]+"|"+ (Integer.parseInt(split[1]) + 1));
            return item;
        }).collect(Collectors.toList());

        // 获取根节点
        JsmindDataVo.JsMindData rootNode = mergeNodes.stream().filter(node -> node.getLevel() == 1).collect(Collectors.toList()).get(0);
        String PTopicByRootNode= rootNode.getTopic() + "|" + rootNode.getLevel();
        // 获取二级节点
        List<JsmindDataVo.JsMindData> node2s = mergeNodes.stream().filter(item -> item.getLevel() == 2).collect(Collectors.toList());
        // 修改时间节点上下级节点连接数据
        node2s.forEach(node2->{
            // 生成时间节点
            List<JsmindDataVo.JsMindData> dateNodes = getDateNodesByDateDimension(node2);
            dateNodes.forEach(dateNode->{
                dateNode.setPTopic(PTopicByRootNode);
                dateNode.setLevel(2);

                List<String> topics = resultNodes.stream().map(item -> item.getTopic()).collect(Collectors.toList());
                // 拷贝原来的二级节点
                JsmindDataVo.JsMindData copyNode2 = JSON.parseObject(JSON.toJSONString(node2), JsmindDataVo.JsMindData.class);
                String PtopicByDateNode = dateNode.getTopic() + "|" +dateNode.getLevel();
                copyNode2.setLevel(3);
                copyNode2.setPTopic(PtopicByDateNode);
                resultNodes.add(copyNode2);
                resultNodes.add(dateNode);
//                if(!topics.contains(dateNode.getTopic())){
//                    resultNodes.add(dateNode);
//                }
            });
        });

        // 组织成思维导图
        JsmindUtil.generateJsmindBydateDimension(rootNode,resultNodes,null);
        Jsmind jsmind = JsmindUtil.getJsmindByRootNode(rootNode).setMindMapName("时间维度");
        return jsmind;
    }

    private List<JsmindDataVo.JsMindData> getDateNodesByDateDimension(JsmindDataVo.JsMindData node2) {
        List<JsmindDataVo.DateFileMapping> dateFileMappings = node2.getDateFileMapping();
        // 生成时间节点
        List<JsmindDataVo.JsMindData> dateNodes = new ArrayList<>();
        Set<String> datesAll = new HashSet<>();
        if(!CollectionUtil.isEmpty(dateFileMappings)){
            dateFileMappings.forEach(dateFileMapping -> {
                Set<String> dates = dateFileMapping.getDates();
                datesAll.addAll(dates);
            });
        }
        datesAll.forEach(date->{
            JsmindDataVo.JsMindData newNode = new JsmindDataVo.JsMindData();
            newNode.setTopic(date);
            newNode.setPTopic(node2.getTopic()+"|"+node2.getLevel());
            newNode.setLevel(node2.getLevel() + 1);
            dateNodes.add(newNode);
        });
        return dateNodes;
    }

}
