package com.ywjs.jcsy.factory;

/**
 * 实体类工厂类
 * @author zhouwei
 * @create 2020-11-24 14:36
 */
public class EntityFactory {

    public static<T> T create(Class<? extends T> clazz){
        try {
            if(null != clazz){
                return  clazz.newInstance();
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
}
