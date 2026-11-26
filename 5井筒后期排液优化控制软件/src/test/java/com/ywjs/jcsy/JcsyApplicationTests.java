package com.ywjs.jcsy;

import cn.hutool.core.io.FileUtil;
import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.ywjs.jcsy.common.enums.ManagementTypeEnum;
import com.ywjs.jcsy.entity.File;
import com.ywjs.jcsy.entity.Jsmind;
import com.ywjs.jcsy.entity.Project;
import com.ywjs.jcsy.factory.EntityFactory;
import com.ywjs.jcsy.mapper.ProjectMapper;
import com.ywjs.jcsy.service.FileService;
import com.ywjs.jcsy.service.JsmindService;
import com.ywjs.jcsy.vo.JsmindDataVo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RunWith(SpringRunner.class)
@SpringBootTest
public class JcsyApplicationTests {

    @Autowired
    JsmindService jsmindService;
    @Autowired
    FileService fileService;
    @Autowired
    JdbcTemplate jdbcTemplate;

    @Autowired
    ProjectMapper projectMapper;
    /**
     * 获取数据库的所有表的名字
     */
    @Test
    public void sss(){
//        List<String> ll = jdbcTemplate.queryForList("select tbl_name from sqlite_master order by type",String.class);
//        ll.forEach(t -> System.out.println(t));
        List<Map<String, Object>> projectsAndFileCount = projectMapper.getProjectsAndFileCount();
        System.out.println(projectsAndFileCount);

    }

    @Test
    public void testGroupBy(){
        List<Jsmind> jsminds = jsmindService.list();
        // 获取二级节点
        List<List<JsmindDataVo.JsMindData>> level2NodeList = jsminds.stream().map(jsmind -> jsmind.getData().getData().getChildren()).collect(Collectors.toList());
        System.out.println(level2NodeList);
        // 扁平化
        List<JsmindDataVo.JsMindData> level2Nodes = level2NodeList.stream().flatMap(a -> a.stream()).collect(Collectors.toList());
        //
        Map<String, Map<String, List<JsmindDataVo.JsMindData>>> collect = level2Nodes.stream().collect(Collectors.groupingBy(JsmindDataVo.JsMindData::getTopic, Collectors.groupingBy(JsmindDataVo.JsMindData::getId)));
        System.out.println(collect);


    }

    @Test
    public void jsmindServiceTest(){
//        List<Jsmind> jsminds = jsmindService.list(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getId,"1602320047127_b1bb002821484327b96e1a4928dce101"));
//        System.out.println(jsminds);
//        List<Jsmind> jsminds2 = jsmindService.list(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getId,"1602320047127_b1bb002821484327b96e1a4928dce101"));
//        System.out.println(jsminds2);

        Page<File> page = new Page<>(1, 1);
        IPage<File> fileIPage = new com.ywjs.jcsy.entity.File().selectPage(page, null);
        List<File> records = fileIPage.getRecords();
        System.out.println(records);
        IPage<File> fileIPage2 = new com.ywjs.jcsy.entity.File().selectPage(page, null);
        List<File> records2 = fileIPage2.getRecords();
        System.out.println(records);
    }

    @Test
    public void testFactroy(){
        Jsmind jsmind = EntityFactory.create(Jsmind.class);
        List<Jsmind> jsminds = jsmind.selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getId,"1602320047127_b1bb002821484327b96e1a4928dce101"));
        System.out.println(jsminds);
        List<Jsmind> jsminds2 = jsmind.selectList(new LambdaQueryWrapper<Jsmind>().eq(Jsmind::getId,"1602320047127_b1bb002821484327b96e1a4928dce101"));
        System.out.println(jsminds2);
//        Model<Jsmind> jsmindModel = EntityFactory.create(Jsmind.class,new Model<Jsmind>());
//        List<Jsmind> jsminds1 = jsmindModel.selectAll();
//        System.out.println(jsminds1);

//        List<Jsmind> jsminds = new Jsmind().selectAll();
//        System.out.println(jsminds);
//
//        List<Jsmind> list = jsmindService.list();
//        System.out.println(list);
    }

    @Test
    public void test111(){
        ArrayList<String> strings = new ArrayList<>();
        strings.add("小明");
        strings.add("好好");
        String jsonString = JSON.toJSONString(strings);
        System.out.println(jsonString);
        System.out.println(jsonString);
    }

    @Test
    public void testDistingct(){
        QueryWrapper<File> wrapper = new QueryWrapper<>();
        wrapper.select("distinct file_type");
        List<File> files = fileService.getBaseMapper().selectList(wrapper);
        List<String> fileType = files.stream().map(File::getFileType).collect(Collectors.toList());
        System.out.println(fileType);
    }

    @Test
    public void test2(){
        Jsmind jsmind = jsmindService.getBaseMapper().selectById("1602320047914_77c794d9eeca4fc3a5fd9ed8055bb838");
        JsmindDataVo.JsMindData data = jsmind.getData().getData();
        JsmindDataVo.JsMindData jsMindData = new JsmindDataVo.JsMindData();
        List<JsmindDataVo.JsMindData> jsMindData1 = new ArrayList<>();
        List<JsmindDataVo.JsMindData> node = getNode(data, "32ee6e39-b32a-4089-a61d-d25feb813285", jsMindData1);
        System.out.println(jsMindData1);
        System.out.println(node);
    }
    private List<JsmindDataVo.JsMindData> getNode(JsmindDataVo.JsMindData data ,String id,List<JsmindDataVo.JsMindData> nodes){
        if(data.getId().equals(id)){
            nodes.add(data);
        }


        if(data.getChildren() != null && data.getChildren().size() > 0){
            for (JsmindDataVo.JsMindData child : data.getChildren()) {
                 getNode(child,id,nodes);
            }
        }
        return nodes;

    }

    @Test
    public void contextLoads() {
        String currNodeFilePath= "E:\\工作文件\\工作文件\\井场试验全流程知识管理平台（前端）\\测试用例\\OpenwellsProject\\testxmlData\\IDS\\8.19\\ids-cdp_ddr_20140819_080117.xml";
        String targetDir = "E:\\workspace\\jcsy/tmp/OpenwellsProject/OpenwellsProject/testxmlData(0)/IDS(3)/8.18(8)";
//        FileUtil.copy("E:\\test\\压缩文件包1.zip","E:\\test\\zip",false);
        FileUtil.copy(currNodeFilePath,targetDir,false);
    }
//    @Autowired
//    FileService fileService;
//
//    @Autowired
//    OperationMapper operationMapper;





    @Test
    public void boo(){
        File file = new File().setFileId(1306132892388237314L);
        File file1 = file.selectById();
        System.out.println(file1);
    }


    @Test
    public void test(){
//        new Jsmind().setSupportHtml(true).insert();
//        Jsmind jsmind = new Jsmind().setId("1111").selectById();
//        jsmind.setId(null).insert();
//        String data = jsmind.getData();
//        JsmindDataVo jsmindDataVo = JSON.parseObject(data, JsmindDataVo.class);
//        System.out.println(jsmind.getData()  );
//        StringBuffer sb = new StringBuffer();
//        for (int i = 0; i < 1000000; i++) {
//            sb.append("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
//        }
//        new Tag().setTagName(sb.toString()).insert();
        
//        File file = new File().setFileId(2L).selectById();
//        System.out.println(file);

        File file1 = new File().setManagementType(ManagementTypeEnum.UPLOAD);
        file1.insert();
        System.out.println(file1.selectById());
        // 41265
//        CollectionSc collectionSc = ;
//        new CollectionSc().setCollectionId(22l).setCollectionName("dsf").setTargetId(1l).setUserId(1l).setCollectionType(CollectionTypeEnum.PROJECT).insert();
//        System.out.println(new CollectionSc().selectAll());
//        List<FilesVo> fileDetail = fileService.getFileDetail(1, 2);
//        System.out.println(fileDetail);
//        boolean insert = new CollectionSc().setCollectionType(CollectionTypeEnum.FILE).insert();
    }

}
