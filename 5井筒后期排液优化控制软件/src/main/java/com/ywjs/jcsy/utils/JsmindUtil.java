package com.ywjs.jcsy.utils;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.service.FileService;
import com.ywjs.jcsy.vo.CountVo;
import com.ywjs.jcsy.vo.JsmindDataVo;

import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 思维导图工具类
 * @author zhouwei
 * @create 2020-09-23 18:07
 */
public class JsmindUtil {

    /**
     * 检查当前目录是否有指定目录
     * @param srcPath
     * @param dirName
     * @return
     */
    private static boolean checkHasDir(String srcPath,String dirName){
        File[] ls = FileUtil.ls(srcPath + File.separator);
        for (File file : ls) {
            String name = file.getName();
            if(name.equals(dirName)) return true;
        }
        return false;
    }

    /**
     * 合并思维导图
     * @param mindMaps
     */
    public static JsmindDataVo.JsMindData merge(List<JsmindDataVo.JsMindData> mindMaps){
        return null;
    }




    /**
     * 将JsMindData对应文件组织指定目录
     * @param data
     * @param filePath
     */
    public static Map<String,List<String>>  copyFileByJsmind(JsmindDataVo.JsMindData data, String filePath, FileService fileService,Map<String,List<String>> map){
        // 创建目录
        String topic = data.getTopic();
        // 正则匹配过滤图标信息
        topic = topic.substring(topic.lastIndexOf(">")+1, topic.length()).replaceAll("[(]{1}[0-9]+[)]","");
        // 判断当前目录写是否有该目录
        boolean hasDir = checkHasDir(filePath, topic);
        if(hasDir) topic = topic + "-" + UUID.randomUUID().toString().replace("-","");
        String targetDir = filePath + File.separator +topic;
        FileUtil.mkdir(targetDir);
        // 获取改节点下的所有文件路径
        List<Long> fileIds = data.getFileIds();
        if(!CollectionUtil.isEmpty(fileIds)){
            List<com.ywjs.jcsy.entity.File> files = fileService.getBaseMapper().selectBatchIds(fileIds);
            // 拷贝文件到目录,文件名+UUID保证文件名称唯一
            FileUtils.moveFileAndRenameExport(targetDir, files);
            // 将文件名称与节点id映射
            List<File> fileList = Arrays.asList(FileUtil.ls(targetDir));
            // 过滤获取文件
            List<String> filelNames = fileList.stream().filter(item -> item.isFile()).map(File::getName).collect(Collectors.toList());
            if(!CollectionUtil.isEmpty(filelNames)){
                map.put(data.getId(),filelNames);
            }
        }
        // 递归条件
        List<JsmindDataVo.JsMindData> children = data.getChildren();
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item->copyFileByJsmind(item,targetDir,fileService,map));
        }
        return map;
    }


    /**
     * 将JsMindData对应文件组织指定目录
     * @param data
     * @param filePath
     */
    public static void   copyFileUseRealNameByJsmind(JsmindDataVo.JsMindData data, String filePath, FileService fileService){
        if(FileUtil.exist(filePath)){
            // 创建目录
            String topic = data.getTopic();
            // 正则匹配过滤图标信息
            topic = topic.substring(topic.lastIndexOf(">")+1, topic.length()).replaceAll("[(]{1}[0-9]+[)]","");
            // 判断当前目录写是否有该目录
            boolean hasDir = checkHasDir(filePath, topic);
            if(hasDir) topic = topic + "-" + UUID.randomUUID().toString().replace("-","");
            String targetDir = filePath + File.separator +topic;
            FileUtil.mkdir(targetDir);
            // 获取改节点下的所有文件路径
            List<Long> fileIds = data.getFileIds();
            if(!CollectionUtil.isEmpty(fileIds)){
                List<com.ywjs.jcsy.entity.File> files = fileService.getBaseMapper().selectBatchIds(fileIds);
                // 拷贝文件到目录,文件名+UUID保证文件名称唯一
                FileUtils.moveFileUseRename(targetDir, files);
            }
            // 递归条件
            List<JsmindDataVo.JsMindData> children = data.getChildren();
            if(!CollectionUtil.isEmpty(children)){
                children.forEach(item->copyFileUseRealNameByJsmind(item,targetDir,fileService));
            }
        }
    }

    /**
     * 将JsMindData对应文件组织指定目录
     * @param data
     * @param filePath
     */
    public static void   copyFileByJsmind(JsmindDataVo.JsMindData data, String filePath, FileService fileService){
        if(FileUtil.exist(filePath)){
            // 创建目录
            String topic = data.getTopic();
            // 正则匹配过滤图标信息
            topic = topic.substring(topic.lastIndexOf(">")+1, topic.length()).replaceAll("[(]{1}[0-9]+[)]","");
            // 判断当前目录写是否有该目录
            boolean hasDir = checkHasDir(filePath, topic);
            if(hasDir) topic = topic + "-" + UUID.randomUUID().toString().replace("-","");
            String targetDir = filePath + File.separator +topic;
            FileUtil.mkdir(targetDir);
            // 使用可变参数扩展功能，若不传该参数，长度为0，不影响之前代码，若传判断扩展功能
//        if(arg.length > 0){
//            // 将当前配置保存到该目录，思维导图导入时解析
//            try {
//                String jsonString = JSON.toJSONString(data);
//                FileOutputStream out = new FileOutputStream(targetDir+File.separator+"conf.conf");
//                out.write(jsonString.getBytes());
//                out.close();
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        }
            // 获取改节点下的所有文件路径
            List<Long> fileIds = data.getFileIds();
            if(!CollectionUtil.isEmpty(fileIds)){
                List<com.ywjs.jcsy.entity.File> files = fileService.getBaseMapper().selectBatchIds(fileIds);
                // 拷贝文件到目录,文件名+UUID保证文件名称唯一
                FileUtils.moveFileAndRenameExport(targetDir, files);
                // 将文件名称与节点id映射
//                List<File> fileList = Arrays.asList(FileUtil.ls(targetDir));
//                // 过滤获取文件
//                List<String> filelNames = fileList.stream().filter(item -> item.isFile()).map(File::getName).collect(Collectors.toList());
//                if(!CollectionUtil.isEmpty(filelNames)){
//                }
            }
            // 递归条件
            List<JsmindDataVo.JsMindData> children = data.getChildren();
            if(!CollectionUtil.isEmpty(children)){
                children.forEach(item->copyFileByJsmind(item,targetDir,fileService));
            }
        }
    }

    /**
     * 获取思维导图下文件id集合
     * @param JsMindDatas
     * @param fileIds
     */
    public static List<Long> getFileIds(JsmindDataVo.JsMindData JsMindDatas, List<Long> fileIds){
        List<Long> currfileIds = JsMindDatas.getFileIds();
        if(!CollectionUtil.isEmpty(currfileIds)){
            fileIds.addAll(currfileIds);
        }
        // 递归条件
        if(!CollectionUtil.isEmpty(JsMindDatas.getChildren())){
            JsMindDatas.getChildren().forEach(item->getFileIds(item,fileIds));
        }
        return fileIds;
    }


    /**
     * 获取思维导图下指定节点
     * @param jsMindData
     * @param nodeId
     * @return
     */
    public static JsmindDataVo.JsMindData getJsmindDataByNodeId(JsmindDataVo.JsMindData jsMindData, String nodeId){
        if(jsMindData.getId().equals(nodeId)){
            return jsMindData;
        }else {
            jsMindData.getChildren().forEach(item->getJsmindDataByNodeId(item,nodeId));
        }
        return null;
    }


    /**
     * 收起所有节点
     * @param data
     */
    public static void packUpAllNode(JsmindDataVo.JsMindData data){
        data.setExpanded(false);
        List<JsmindDataVo.JsMindData> children = data.getChildren();
        // 递归条件
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item-> packUpAllNode(item) );
        }
    }

    /**
     * 模板修改topic+fileIds
     * @param data
     */
    public static void unMountAndChangeTopic(JsmindDataVo.JsMindData data){
        data.setFileIds(new ArrayList<Long>());
        String topic = data.getTopic();
        // 删除（34）
        topic = topic.replaceAll("[(]{1}[0-9]+[)]","");
        data.setTopic(topic);
        List<JsmindDataVo.JsMindData> children = data.getChildren();
        // 递归条件
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item-> unMountAndChangeTopic(item) );
        }
    }



    /**
     * 生成节点Id，解决首页id重复问题
     * @param data
     */
    public static void generateNodeId(JsmindDataVo.JsMindData data){
        data.setId(UUID.randomUUID().toString());
        List<JsmindDataVo.JsMindData> children = data.getChildren();
        // 递归条件
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item-> generateNodeId(item) );
        }
    }

    public static Jsmind getJsmindByRootNode(JsmindDataVo.JsMindData rootjsMindData){
        // 重新生成各个节点id
        JsmindUtil.generateNodeId(rootjsMindData);

        JsmindDataVo root = new JsmindDataVo();
        root.setFormat("node_tree");
        root.setMeta(new JsmindDataVo.Meta());
        root.setData(rootjsMindData);

        Jsmind jsmind = new Jsmind();
        jsmind.setEditable(false);
        jsmind.setTheme("primary");
        jsmind.setSupportHtml(true);
        jsmind.setMode("full");
        jsmind.setId(UUID.randomUUID().toString());

        jsmind.setData(root);
        return jsmind;

    }

    /**
     *   生成根节点
     * @param topic
     * @return
     */
    public static JsmindDataVo.JsMindData getRootNode(String topic){
        JsmindDataVo.JsMindData rootjsMindData = new JsmindDataVo.JsMindData();
        rootjsMindData.setId(UUID.randomUUID().toString());
        rootjsMindData.setTopic(topic);
        rootjsMindData.setExpanded(true);
        rootjsMindData.setIsroot(true);
        return  rootjsMindData;
    }

    /**
     *   生成节点
     * @param topic
     * @return
     */
    public static JsmindDataVo.JsMindData getNodeByTopicAndFileIds(String topic,List<Long> fileIds){
        JsmindDataVo.JsMindData rootjsMindData = new JsmindDataVo.JsMindData();
        rootjsMindData.setId(UUID.randomUUID().toString());
        rootjsMindData.setTopic(topic);
        rootjsMindData.setExpanded(true);
        rootjsMindData.setIsroot(false);
        rootjsMindData.setFileIds(fileIds);
        return  rootjsMindData;
    }

    /**
     *   生成节点
     * @param topics
     * @return
     */
    public static List<JsmindDataVo.JsMindData> getNodeByTopicAndFileIds(Set<String> topics, List<Long> fileIds){
        List<JsmindDataVo.JsMindData > list = new ArrayList<>();
        topics.forEach(topic->{
            JsmindDataVo.JsMindData rootjsMindData = new JsmindDataVo.JsMindData();
            rootjsMindData.setId(UUID.randomUUID().toString());
            rootjsMindData.setTopic(topic);
            rootjsMindData.setExpanded(true);
            rootjsMindData.setIsroot(false);
            rootjsMindData.setFileIds(fileIds);
            list.add(rootjsMindData);
        });

        return  list;
    }

    /**
     * 根据节点名称生成节点
     * @return
     */
    public static List<JsmindDataVo.JsMindData> getNodeByTopics(Set<String> topics){
        List<JsmindDataVo.JsMindData> list = new ArrayList<>();
        topics.forEach(t->{
            JsmindDataVo.JsMindData rootjsMindData = new JsmindDataVo.JsMindData();
            rootjsMindData.setId(UUID.randomUUID().toString());
            rootjsMindData.setTopic(t);
            rootjsMindData.setExpanded(true);
            rootjsMindData.setIsroot(false);
            list.add(rootjsMindData);
        });
        return  list;
    }

    /**
     * 生成12月节点
     * @return
     */
    public static List<JsmindDataVo.JsMindData> get12MonthNode(){
        List<JsmindDataVo.JsMindData> list = new ArrayList<>();
        for (int i = 1; i <= 12; i++) {
            JsmindDataVo.JsMindData rootjsMindData = new JsmindDataVo.JsMindData();
            rootjsMindData.setId(UUID.randomUUID().toString());
            rootjsMindData.setTopic(i+"月");
            rootjsMindData.setExpanded(true);
            rootjsMindData.setIsroot(false);
            list.add(rootjsMindData);
        }
        return  list;
    }

    /**
     * 计算文件数量
     * @param JsMindDatas
     * @param countNodeFileList
     */
    public static void countNodeFile(List<JsmindDataVo.JsMindData> JsMindDatas,List<CountVo> countNodeFileList){
        JsMindDatas.forEach(data->{
            List<Long> fileIds = data.getFileIds();
            if(!CollectionUtil.isEmpty(fileIds)){
                String topic = data.getTopic();
                // 取出后边（）
                topic = topic.replaceAll("[(]{1}[0-9]+[)]","");
                countNodeFileList.add(new CountVo().setName(topic).setValue(fileIds.size()));
            }
            // 递归条件
            if(!CollectionUtil.isEmpty(data.getChildren())){
                countNodeFile(data.getChildren(),countNodeFileList);
            }
        });
    }


    /**
     * 根据节点id卸载文件
     * @param data
     * @param nodeId
     * @param fileId
     */
    public static void uninstallFileByFileIdByNodeId(JsmindDataVo.JsMindData data,String nodeId, Long fileId){
        // 递归结束条件
        if(data.getId().equals(nodeId)){
            List<Long> fileIds = data.getFileIds();
            if(!CollectionUtil.isEmpty(fileIds)){
                boolean contains = fileIds.contains(fileId);
                if(contains){
                    fileIds = fileIds.stream().filter(item -> !item.equals(fileId)).collect(Collectors.toList());
                    data.setFileIds(fileIds);
                    return;
                }
            }
        }else {
            List<JsmindDataVo.JsMindData> children = data.getChildren();
            children.forEach(item->uninstallFileByFileIdByNodeId(item,nodeId,fileId));
        }
    }

    /**
     * 获取思维导图集合中的节点集合
     * @param jsminds
     * @return
     */
    public static List<JsmindDataVo.JsMindData> getNodes(List<Jsmind> jsminds){
        List<JsmindDataVo.JsMindData> nodes = new ArrayList<>();
        for (Jsmind jsmind : jsminds) {
            JsmindDataVo.JsMindData data = jsmind.getData().getData();
            List<JsmindDataVo.JsMindData> currNodes = getNodes(data, new ArrayList<JsmindDataVo.JsMindData>());
            nodes.addAll(currNodes);
        }
        return nodes;
    }


    /**
     * 获取思维导图集合中的节点集合
     */
    public static void setNodeLevel(JsmindDataVo.JsMindData node,int level){
        node.setLevel(level);
        List<JsmindDataVo.JsMindData> children = node.getChildren();
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item->setNodeLevel(item,level+1));
        }
    }

    public static void setExpanded(JsmindDataVo.JsMindData node, int i,boolean b) {
        int level = node.getLevel();
        if(level>=i){
            node.setExpanded(b);
        }
        List<JsmindDataVo.JsMindData> children = node.getChildren();
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item->setExpanded(item,i,b));
        }
    }

    /**
     * 获取思维导图集合中的节点集合
     * @param jsminds
     * @return
     */
    public static List<JsmindDataVo.JsMindData> getNodesSetLevel(List<Jsmind> jsminds){
        List<JsmindDataVo.JsMindData> nodes = new ArrayList<>();
        for (Jsmind jsmind : jsminds) {
            JsmindDataVo.JsMindData data = jsmind.getData().getData();
            List<JsmindDataVo.JsMindData> currNodes = getNodesSetLevel(data, new ArrayList<>(),1);
            nodes.addAll(currNodes);
        }
        return nodes;
    }

    /**
     * 获取当前节点及子节点集合（平级）
     * @param node
     * @param nodes
     * @return
     */
    private static List<JsmindDataVo.JsMindData> getNodesSetLevel(JsmindDataVo.JsMindData node, List<JsmindDataVo.JsMindData> nodes, int level){
        node.setLevel(level);
        nodes.add(node);
        List<JsmindDataVo.JsMindData> children = node.getChildren();
        if(!CollectionUtil.isEmpty(children)){
            int newLevel =  level + 1;
            children.forEach(item-> getNodesSetLevel(item,nodes,newLevel));
        }
        return nodes;
    }


    /**
     * 获取某个思维导图中的节点集合
     * @param jsmind
     * @return
     */
    public static List<JsmindDataVo.JsMindData> getNodes(Jsmind jsmind){
        JsmindDataVo.JsMindData data = jsmind.getData().getData();
        return getNodes(data, new ArrayList<JsmindDataVo.JsMindData>());
    }


    /**
     * 获取当前节点及子节点集合（平级）
     * @param node
     * @param nodes
     * @return
     */
    private static List<JsmindDataVo.JsMindData> getNodes(JsmindDataVo.JsMindData node, List<JsmindDataVo.JsMindData> nodes){
        nodes.add(node);
        List<JsmindDataVo.JsMindData> children = node.getChildren();
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item-> getNodes(item,nodes));
        }
        return nodes;
    }

    /**
     * 获取当前节点及子节点集合（平级）
     */
    public static List<JsmindDataVo.JsMindData> getNodeByNodeId(JsmindDataVo.JsMindData node, String nodeId, List<JsmindDataVo.JsMindData> result){
        if(nodeId.equals(node.getId())){
            result.add(node);
        }
        List<JsmindDataVo.JsMindData> children = node.getChildren();
        if(!CollectionUtil.isEmpty(children)){
            for (JsmindDataVo.JsMindData item : children) {
                getNodeByNodeId(item,nodeId,result);
            }
        }
        return result;
    }

    /**
     * 根据节点id获取父节点
     * @param node
     * @param nodeId
     * @param result
     * @return
     */
    public static List<JsmindDataVo.JsMindData> getParentNodeByChildId(JsmindDataVo.JsMindData node, String nodeId, List<JsmindDataVo.JsMindData> result){
        List<JsmindDataVo.JsMindData> children = node.getChildren();
        List<String> ids = children.stream().map(item -> item.getId()).collect(Collectors.toList());
        if(ids.contains(nodeId)){
            result.add(node);
        }
        if(!CollectionUtil.isEmpty(children)){
            for (JsmindDataVo.JsMindData item : children) {
                getNodeByNodeId(item,nodeId,result);
            }
        }
        return result;
    }

    public static void changeDirection(JsmindDataVo.JsMindData copyNode, String direction) {
        copyNode.setDirection(direction);
        List<JsmindDataVo.JsMindData> children = copyNode.getChildren();
        if(!CollectionUtil.isEmpty(children)){
            for (JsmindDataVo.JsMindData child : children) {
                changeDirection(child,direction);
            }
        }
    }

    public static void sortByDate(List<JsmindDataVo.JsMindData> childrens) {
        childrens.sort((o1, o2) -> {
            String topic1 = o1.getTopic();
            String topic2 = o1.getTopic();
            // 转化为时间戳
            DateTime time1 = DateUtil.parse(topic1, "yyyy-MM");
            DateTime time2 = DateUtil.parse(topic2, "yyyy-MM");
            long l = time2.getTime() - time1.getTime();
            return Integer.parseInt(String.valueOf(l));
        });
    }


    /**
     * 合并
     * @param nodes
     * @return
     */
    public static List<JsmindDataVo.JsMindData> distinctAndMerge(List<JsmindDataVo.JsMindData> nodes) {
        List<JsmindDataVo.JsMindData> distincts = new ArrayList<>();
//        Map<String, List<JsmindDataVo.JsMindData>> groupByTopicMap = nodes.stream().collect(Collectors.groupingBy(node -> node.getTopic() + "--" +node.getLevel()+"--"+node.getPTopic()));
        Map<String, List<JsmindDataVo.JsMindData>> groupByTopicMap = nodes.stream().collect(Collectors.groupingBy(node -> node.getTopic() + "--" +node.getLevel()+"--"+node.getPTopic()));
        for (Map.Entry<String, List<JsmindDataVo.JsMindData>> entry : groupByTopicMap.entrySet()) {
            String[] split = entry.getKey().split("--");
            String topic = split[0];
            int level = Integer.parseInt(split[1]);

            List<JsmindDataVo.JsMindData> value = entry.getValue();
            if (!CollectionUtil.isEmpty(value)) {
                JsmindDataVo.JsMindData node = new JsmindDataVo.JsMindData();
                node.setTopic(topic);
                node.setLevel(level);
                List<JsmindDataVo.DateFileMapping> mappings = value.stream().filter(item->item.getDateFileMapping() != null).map(item -> item.getDateFileMapping()).flatMap(a->a.stream()).collect(Collectors.toList());
                node.setDateFileMapping(mappings);
                node.setPTopic(value.get(0).getPTopic());
                List<Long> fileIds = value.stream().filter(item -> !CollectionUtil.isEmpty(item.getFileIds())).map(item -> item.getFileIds()).flatMap(item -> item.stream()).collect(Collectors.toList());
                node.setFileIds(fileIds);
                distincts.add(node);
            }
        }
        return distincts;
    }


    public static void setDateFileMapping(List<Jsmind> jsminds) {
        // 获取二级节点
        jsminds.forEach(jsmind -> {
            JsmindDataVo.JsMindData rootNode = jsmind.getData().getData();
            setNodeDateAndDateFileMapping(rootNode,null,null);
        });

    }


    /**
     * 递归设置时间
     * @param node
     * @return
     */
    private static void setNodeDateAndDateFileMapping(JsmindDataVo.JsMindData node,String startTime,String endTime){
        int level = node.getLevel();
        if(level == 2){
            Set<String> dates = DateUtils.getDateWithBetweenMonth(node.getStartTime(), node.getEndTime());
            List<Long> fileIds = node.getFileIds();
            JsmindDataVo.DateFileMapping dateFileMapping = new JsmindDataVo.DateFileMapping();
            dateFileMapping.setFileIds(fileIds);
            dateFileMapping.setDates(dates);
            node.setDateFileMapping(Arrays.asList(dateFileMapping));
        }
        if(level > 2){
            node.setStartTime(startTime);
            node.setEndTime(endTime);
            Set<String> dates = DateUtils.getDateWithBetweenMonth(startTime, endTime);
            List<Long> fileIds = node.getFileIds();
            JsmindDataVo.DateFileMapping dateFileMapping = new JsmindDataVo.DateFileMapping();
            dateFileMapping.setFileIds(fileIds);
            dateFileMapping.setDates(dates);
            node.setDateFileMapping(Arrays.asList(dateFileMapping));
        }
        List<JsmindDataVo.JsMindData> children = node.getChildren();
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item-> setNodeDateAndDateFileMapping(item,node.getStartTime(),node.getEndTime()));
        }
    }

    public static void setPTopic(List<Jsmind> jsminds) {
        // 获取二级节点
        jsminds.forEach(jsmind -> {
            JsmindDataVo.JsMindData rootNode = jsmind.getData().getData();
            setPTopic(rootNode,null);
        });
    }


    private static void setPTopic(JsmindDataVo.JsMindData node, JsmindDataVo.JsMindData  pNode) {

        if(pNode != null){
            node.setPTopic(pNode.getTopic() + "|" + pNode.getLevel());
        }
        List<JsmindDataVo.JsMindData> childrens = node.getChildren();
        if(!CollectionUtil.isEmpty(childrens)){
            childrens.forEach(children->setPTopic(children,node));
        }
    }

    public static void setPid(List<Jsmind> jsminds) {
        // 获取二级节点
        jsminds.forEach(jsmind -> {
            JsmindDataVo.JsMindData rootNode = jsmind.getData().getData();
            setPid(rootNode,null);
        });
    }

    private static void setPid(JsmindDataVo.JsMindData node, String pid) {
        node.setPid(pid);
        List<JsmindDataVo.JsMindData> childrens = node.getChildren();
        if(!CollectionUtil.isEmpty(childrens)){
            childrens.forEach(children->setPid(children,node.getId()));
        }
    }

    public static void generateJsmind(JsmindDataVo.JsMindData node, List<JsmindDataVo.JsMindData> resultNodes) {
        String PTopic = node.getTopic() +"|"+ node.getLevel();
        // 获取子节点
        List<JsmindDataVo.JsMindData> childrens = resultNodes.stream().filter(itemNode -> itemNode.getPTopic().equals(PTopic)).collect(Collectors.toList());

        if(!CollectionUtil.isEmpty(node.getDateFileMapping())){
            List<String> dates = node.getDateFileMapping().stream().map(item -> item.getDates()).flatMap(a -> a.stream()).collect(Collectors.toList());
            childrens = childrens.stream().filter(item->{
                if(DateUtils.checkDate(item.getTopic()) && dates.contains(item.getTopic()))  return true;
                if (!DateUtils.checkDate(item.getTopic())) return true;
                return false;
            }).collect(Collectors.toList());
        }
        List<JsmindDataVo.JsMindData> jsMindData = JSON.parseObject(JSON.toJSONString(childrens), new TypeReference<List<JsmindDataVo.JsMindData>>() { });
        node.setChildren(jsMindData);
        jsMindData.forEach(children->generateJsmind(children,resultNodes));
    }

    /**
     * 时间维度生成思维导图
     * @param node
     * @param resultNodes
     */
    public static void generateJsmindBydateDimension(JsmindDataVo.JsMindData node, List<JsmindDataVo.JsMindData> resultNodes,String date) {
        String PTopic = node.getTopic() +"|"+ node.getLevel();
        // 获取 2级节点时间映射
        String node2ate = date;

        if(node.getLevel() > 2){
            // 获取子节点
            List<JsmindDataVo.JsMindData> childrens = resultNodes.stream().filter(itemNode -> itemNode.getPTopic().equals(PTopic)).collect(Collectors.toList());

            String finalNode2ate = node2ate;
            childrens = childrens.stream().filter(item->{
                List<String> childrenDates = item.getDateFileMapping().stream().map(children -> children.getDates()).flatMap(a -> a.stream()).collect(Collectors.toList());
                boolean contains = childrenDates.contains(finalNode2ate);
                return contains;
            }).collect(Collectors.toList());
            // 过滤时间在node2ates 的节点
            node.setChildren(childrens);
            List<JsmindDataVo.JsMindData> copyNodes = JSON.parseObject(JSON.toJSONString(childrens), new TypeReference<List<JsmindDataVo.JsMindData>>() {});
            node.setChildren(copyNodes);
            for (JsmindDataVo.JsMindData children : copyNodes) {
                generateJsmindBydateDimension(children,resultNodes, node2ate);
            }
        }else {
            if(node.getLevel() == 2){
                node2ate = node.getTopic();
            }
            // 获取子节点
            List<JsmindDataVo.JsMindData> childrens = resultNodes.stream().filter(itemNode -> itemNode.getPTopic().equals(PTopic)).collect(Collectors.toList());
            // 深拷贝
            List<JsmindDataVo.JsMindData> copyNodes = JSON.parseObject(JSON.toJSONString(childrens), new TypeReference<List<JsmindDataVo.JsMindData>>() {});
            node.setChildren(copyNodes);
            for (JsmindDataVo.JsMindData children : copyNodes) {
                generateJsmindBydateDimension(children,resultNodes, node2ate);
            }

        }



    }

    /**
     * 打散合并思维导图
     * @param jsminds
     * @return
     */
    public static List<JsmindDataVo.JsMindData> mergeNodesByTypeDimension(List<Jsmind> jsminds) {
        // 打散
        List<JsmindDataVo.JsMindData> nodes = JsmindUtil.getNodesSetLevel(jsminds);
        // 设置Ptopic
        JsmindUtil.setTheSamePTopic(jsminds);
        // 合并
        List<JsmindDataVo.JsMindData> mergeNodes = JsmindUtil.distinctAndMerge(nodes);
        return mergeNodes;
    }


    /**
     * 打散合并思维导图
     * @param jsminds
     * @return
     */
    public static List<JsmindDataVo.JsMindData> mergeNodes(List<Jsmind> jsminds) {
        // 打散
        List<JsmindDataVo.JsMindData> nodes = JsmindUtil.getNodesSetLevel(jsminds);
        // 设置Ptopic
        JsmindUtil.setPTopic(jsminds);
        // 递归设置时间文件节点关系
        JsmindUtil.setDateFileMapping(jsminds);
        // 合并
        List<JsmindDataVo.JsMindData> mergeNodes = JsmindUtil.distinctAndMerge(nodes);
        return mergeNodes;
    }

    public static void setTheSamePTopic(List<Jsmind> jsminds) {
        // 获取二级节点
        jsminds.forEach(jsmind -> {
            JsmindDataVo.JsMindData rootNode = jsmind.getData().getData();
            setTheSamePTopic(rootNode,null);
        });
    }


    private static void setTheSamePTopic(JsmindDataVo.JsMindData node, String  PTopic) {
        node.setPTopic(PTopic);
        if(node.getLevel() == 2){
            PTopic = node.getTopic() + "|" + node.getLevel();
        }
        List<JsmindDataVo.JsMindData> childrens = node.getChildren();
        if(!CollectionUtil.isEmpty(childrens)){
            for (JsmindDataVo.JsMindData children : childrens) {
                setTheSamePTopic(children,PTopic);
            }
        }
    }

    public static void nodeSort(JsmindDataVo.JsMindData data) {
        List<JsmindDataVo.JsMindData> children = data.getChildren();
        // 节点排序
        children.sort(Comparator.comparing(item->item.getTopic()));
        if(!CollectionUtil.isEmpty(children)){
            children.forEach(item->nodeSort(item));
        }
    }


}
