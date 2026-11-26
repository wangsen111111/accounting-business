package com.ywjs.jcsy.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;

import java.util.Date;

/**
 * @author zhouwei
 * @create 2020-08-13 17:41
 */

//@Component
public class MetaObjectHandlerConfig implements MetaObjectHandler {
    @Override
    public void insertFill(MetaObject metaObject) {
        Object createTime = null;
        Object updateTime = null;
        if(metaObject.hasGetter("createTime") ){
            createTime = getFieldValByName("createTime", metaObject);
        }
        if(metaObject.hasGetter("updateTime")){
            updateTime = getFieldValByName("updateTime", metaObject);
        }
        Object id = getFieldValByName("id", metaObject);
        Date now = new Date();
        if (createTime == null && metaObject.hasSetter("createTime")){
            setFieldValByName("createTime",now, metaObject);
        }
        if (updateTime == null && metaObject.hasSetter("updateTime")){
            setFieldValByName("updateTime",now, metaObject);
        }
        if (id == null){
//            setFieldValByName("id", SnowFlakeIDGenerator.getInstance().nextId(), metaObject);
        }
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if(metaObject.hasSetter("updateTime")){
            setFieldValByName("updateTime", new Date(), metaObject);
        }
    }
}