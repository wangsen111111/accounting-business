//package com.wu.dao;
//import java.util.ArrayList;
//        import java.util.Collections;
//        import java.util.Random;
//
//public class 红 {
//    public static void main(String[] args) {
//        // 定义一个包含六个数字的列表
//        ArrayList<Integer> numbers = new ArrayList<>();
//        //006 1：1，008 2：1，011 2：2，016 1：1，018 1：1                       019 1：2
//        Integer[] a=[6,8,11,16,18,19];
//        for (int i = 1; i <= 6; i++) {
//            numbers.add(a[i]);
//        }
//
//        // 使用Random对象来生成随机索引
//        Random random = new Random();
//
//        // 用于存放选中的三个数字
//        ArrayList<Integer> selectedNumbers = new ArrayList<>();
//
//        // 从numbers列表中随机选取三个不同的数字
//        while (selectedNumbers.size() < 3) {
//            int index = random.nextInt(numbers.size());
//            selectedNumbers.add(numbers.get(index));
//            numbers.remove(index); // 移除已选中的数字以避免重复选择
//        }
//
//        // 输出选中的三个数字
//        System.out.println("006 1：1，008 2：1，011 2：2，016 1：1，018 1：1                       019 1：2:三场过二关必红 " + selectedNumbers);
//    }
//}