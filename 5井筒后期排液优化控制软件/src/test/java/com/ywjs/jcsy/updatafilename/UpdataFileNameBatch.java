package com.ywjs.jcsy.updatafilename;


import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.ZipUtil;
import org.junit.Test;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author zhouwei
 * @create 2020-08-07 23:44
 */
public class UpdataFileNameBatch {





    @Test
    public void testGetDateRange() throws Exception {

        String s1 = "井场试验|1";
        String s2 = "井场试验|1";
        System.out.println(s1.equals(s2));

//        Set<String> endDay = getEndDay("2020-12-10", "2021-1-20");
//        System.out.println(endDay);

//        List<String> monthBetween = getMonthBetween("2020-12-10", "2021-1-20");
//        System.out.println(monthBetween);
    }

    public static List<String> getMonthBetween(String minDate, String maxDate) throws Exception {
        ArrayList<String> result = new ArrayList<String>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        min.setTime(sdf.parse(minDate));
        min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);// 设置年月日，最少3个参数
        max.setTime(sdf.parse(maxDate));
        max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
        Calendar curr = min;
        while (curr.before(max)) {
            result.add(sdf.format(curr.getTime()));
            curr.add(Calendar.MONTH, 1);
        }
        return result;
    }

    public Set<String> getEndDay(String beginDate, String endDate) throws Exception {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");
        Date dBegin = sdf.parse(beginDate);
        Date dEnd = sdf.parse(endDate);
        Set<String> datas = findDates(dBegin, dEnd);
        return datas;
    }



    public Set<String> findDates(Date dBegin, Date dEnd) {
        Set<String> lDate = new HashSet<>();
        SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd");
        lDate.add(sd.format(dBegin));
        Calendar calBegin = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calBegin.setTime(dBegin);
        Calendar calEnd = Calendar.getInstance();
        // 使用给定的 Date 设置此 Calendar 的时间
        calEnd.setTime(dEnd);
        // 测试此日期是否在指定日期之后
        while (dEnd.after(calBegin.getTime())) {
            // 根据日历的规则，为给定的日历字段添加或减去指定的时间量
            calBegin.add(Calendar.DAY_OF_MONTH, 1);
            lDate.add(sd.format(calBegin.getTime()));
        }
        return lDate;
    }

    @Test
    public void flapMapTest() {
        List<List<Integer>> collect = Stream.of(Arrays.asList(1, 2, 3, 4, 5, 6), Arrays.asList(8, 9, 10, 11, 12)).collect(Collectors.toList());
        System.out.println(collect);
        List<Integer> list = (List<Integer>) Stream.of(Arrays.asList(1, 2, 3, 4, 5, 6), Arrays.asList(8, 9, 10, 11, 12))
                .flatMap(test -> test.stream()).collect(Collectors.toList());
        System.out.println(list);


    }

    @Test
    public void testMd5() throws Exception {

        ArrayList<Long> longs = new ArrayList<>();
        longs.add(1L);
        longs.add(2L);
        longs.add(3L);
        longs.add(4L);
        longs.add(5L);
        List<Long> collect = longs.stream().filter(item -> item.equals(1L)).collect(Collectors.toList());
        System.out.println(collect);


//        ZipUtil.zip("E:\\workspace\\jcsy\\tmp\\aa\\知识管理", "E:\\workspace\\jcsy\\tmp\\aa\\知识管理.zip", true);

//        FileUtil.copy("E:\\偏岩古镇\\13年.jpg",System.getProperty("user.dir")+"/file",false);

        // 13年 - 副本.jpg
//        FileInputStream fileInputStream = new FileInputStream(new File("E:\\偏岩古镇\\13年.jpg"));
//
//        System.out.println(DigestUtils.md5Hex(FileUtil.readBytes(new File("E:\\偏岩古镇\\13年.jpg"))));
//        FileInputStream fileInputStream2 = new FileInputStream(new File("E:\\偏岩古镇\\13年 - 副本.jpg"));
//        System.out.println(DigestUtils.md5Hex(FileUtil.readBytes(new File("E:\\偏岩古镇\\13年 - 副本.jpg"))));
//        // 密码加密存储
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        String encode = passwordEncoder.encode("");


        // 对比明文密码和加密密码是否匹配
//        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
//        boolean matches = passwordEncoder.matches(passWord, passwordDB);
    }

    @Test
    public void variable() {
        String s = "dsfd_fdsfdsfds_sfd.txt";
        int i = s.indexOf("_");
        String substring = s.substring(i + 1, s.length());
        System.out.println(substring);
    }

    @Test
    public void variableTest() {
        ZipUtil.zip("E:\\workspace\\jcsy\\tmp\\哈哈哈分", "E:\\workspace\\jcsy\\tmp\\哈哈哈分.zip", true);
//        variable("qq");
    }

    @Test
    public void testPath() {
//        topic = topic.replaceAll("[(]{1}[0-9]+[)]","");
        String s = "<img style=\"width:25px;height:25px\" src=\"/svg/priority/priority1.svg\"><img style=\"width:25px;height:25px\" src=\"/svg/progress/progress78.svg\">测试思维导图2\\<img style=\"width:25px;height:25px\" src=\"/svg/priority/priority3.svg\"><img style=\"width:25px;height:25px\" src=\"/svg/progress/progress18.svg\"><img style=\"width:25px;height:25px\" src=\"/svg/priority/priority3.svg\"><img style=\"width:25px;height:25px\" src=\"/svg/progress/progress18.svg\">哈哈呵呵(1)";
        String substring = s.substring(s.lastIndexOf(">") + 1, s.length());
        String regx = "[<][.]*[>]";
        String replace = s.replaceAll(regx, "");
        System.out.println(substring);//user.dir指定了当前的路径
    }

    public void test() {
        File[] files = FileUtil.ls("E:\\学习资源\\尚硅谷\\谷粒商城\\高可用集群\\视频\\视频");
        List<File> fileList = Arrays.asList(files);

        for (File file : fileList) {
            String trim = StrUtil.trim(file.getName());
            File dest = new File(file.getParent() + "\\" + trim);
            file.renameTo(dest);

        }
//        List<File> collect = fileList.stream().sorted((o1,o2)->{
//            String o1Str = o1.getName().replace("谷粒学院(", "").replace(").mp4", "");
//            String o2Str = o2.getName().replace("谷粒学院(", "").replace(").mp4", "");
////            String o1Str = o1.getName().substring(0, o1.getName().lastIndexOf("."));
////            String o2Str = o2.getName().substring(0, o2.getName().lastIndexOf("."));
//            Integer v1 = Integer.parseInt(o1Str);
//            Integer v2 = Integer.parseInt(o2Str);
//            return v1.compareTo(v2);
//        }).collect(Collectors.toList());
//        collect.forEach(item-> System.out.println(item.getName()));
//
//        List<String> newNames = IoUtil.readLines(new FileReader("C:\\Users\\zhouwei\\Desktop\\filenames.txt"), new ArrayList<String>());
//        newNames = newNames.stream().filter(item -> item.contains("课时")).map(item->item.split(":")[1]).collect(Collectors.toList());
//        for (int i = 0; i < collect.size(); i++) {
//            String path = collect.get(i).getPath();
//            String parent = collect.get(i).getParent();
//            String newName = newNames.get(i) + ".mp4";
//            String str = StrUtil.format("old => {} new=>{}", collect.get(i).getName(), newName);
//            StrUtil.format(str);
//            System.out.println(str);
//            File dest = new File(parent+"\\"+ newName) ;
//            collect.get(i).renameTo(dest);
//        }
    }

    public static void main(String[] args) {
        List<String> list1 = Arrays.asList(new String[]{"1", "2", "3", "5", "6", "7", "8"}); // 数据库
        List<String> list2 = Arrays.asList(new String[]{"1", "2", "3", "5", "6", "7", "10"}); // 前端
//        List<String> list2 = Arrays.asList(new String[]{"2","7","10"});
        //差集 list1-list2
        List<String> collect2 = list1.stream().filter(num -> !list2.contains(num)).collect(Collectors.toList());
        List<String> collect3 = list2.stream().filter(num -> !list1.contains(num)).collect(Collectors.toList());
        String format = StrUtil.format("差集list1-list2={}", collect2);
        System.out.println(format);
        System.out.println(StrUtil.format("差集list2-list1={}", collect3));


        //交集
//        List<String> collect1 = list1.stream().filter(num -> list2.contains(num))
//                .collect(Collectors.toList());
//        System.out.println("交集");
//        collect1.stream().forEach(System.out::println);
//
//
//
//        //差集list2-list1
//        List<String> collect3 = list2.stream().filter(num -> !list1.contains(num))
//                .collect(Collectors.toList());
//        System.out.println("差集list2-list1");
//        collect3.stream().forEach(System.out::println);
//
//        //并集  不去重
//        list1.addAll(list2);
//        System.out.println("并集  不去重");
//        list1.stream().forEach(System.out::println);
//
//        //并集  去重
//        List<String> collect4 = list1.stream().distinct().collect(Collectors.toList());
//        System.out.println("并集  去重");
//        collect4.stream().forEach(System.out::println);
    }
}
