package com.ywjs.jcsy.utils;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.StrUtil;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author zhouwei
 * @create 2020-12-15 19:13
 */
public class DateUtils {

    /**
     * 正则表达式验证日期格式
     * @param args
     */
    public static void main(String[] args){
        String checkValue = "2007-02";
        System.out.println(checkDate(checkValue));
    }

    /**
     * 校验是否为时间格式
     * @param checkValue
     * @return
     */
    public static boolean checkDate(String checkValue){
//        String eL = "^\\d{4}(\\-|\\/|\\.)\\d{1,2}\\1\\d{1,2}$";
        String eL = "^\\d{4}(\\-|\\/|\\.)\\d{1,2}$";
        Pattern p = Pattern.compile(eL);
        Matcher m = p.matcher(checkValue);
        boolean b = m.matches();
        return b;
    }

    public static Set<String> getMonthBetween(String minDate, String maxDate)  {
        Set<String> result = new HashSet<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月
        Calendar min = Calendar.getInstance();
        Calendar max = Calendar.getInstance();
        try {
            min.setTime(sdf.parse(minDate));
            min.set(min.get(Calendar.YEAR), min.get(Calendar.MONTH), 1);// 设置年月日，最少3个参数
            max.setTime(sdf.parse(maxDate));
            max.set(max.get(Calendar.YEAR), max.get(Calendar.MONTH), 2);
            Calendar curr = min;
            while (curr.before(max)) {
                result.add(sdf.format(curr.getTime()));
                curr.add(Calendar.MONTH, 1);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return result;
    }
    public static  String  getMonth(String dateStr){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM");// 格式化为年月
        return DateUtil.format(DateUtil.parse(dateStr, sdf), sdf);
    }

    public static Set<String> getDateWithBetweenMonth(String startTime, String endTime)  {
        Set<String> monthBetween = new HashSet<>();
        if(!StrUtil.isEmpty(startTime) && !StrUtil.isEmpty(endTime)){
            // 计算该时间范围类的时间
            try {
                monthBetween.addAll( DateUtils.getMonthBetween(startTime, endTime));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else {
            if(!StrUtil.isEmpty(startTime)) monthBetween.add(DateUtils.getMonth(startTime));
            if(!StrUtil.isEmpty(endTime)) monthBetween.add(DateUtils.getMonth(endTime));
        }
        return monthBetween;
    }
}
