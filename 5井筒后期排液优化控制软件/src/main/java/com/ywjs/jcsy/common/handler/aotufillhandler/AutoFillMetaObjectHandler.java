package com.ywjs.jcsy.common.handler.aotufillhandler;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @author zhouwei
 * @create 2020-09-01 9:43
 */
@Slf4j
@Component
public class AutoFillMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        if(metaObject.hasGetter("createTime")) setFieldValByName("createTime", new Date(), metaObject);
        if(metaObject.hasGetter("updateTime")) setFieldValByName("updateTime", new Date(), metaObject);
        if(metaObject.hasGetter("createUser")) setFieldValByName("createUser", "测试用户", metaObject);
        if(metaObject.hasGetter("updateUser")) setFieldValByName("updateUser", "测试用户", metaObject);
        // ---------思维导图默认填充字段-------------
        if(metaObject.hasGetter("mode")) setFieldValByName("mode", "full", metaObject);
        if(metaObject.hasGetter("supportHtml")) setFieldValByName("supportHtml", true, metaObject);
        if(metaObject.hasGetter("editable")) setFieldValByName("editable", true, metaObject);
        if(metaObject.hasGetter("isTemplate")) setFieldValByName("isTemplate", false, metaObject);
        if(metaObject.hasGetter("mode")) setFieldValByName("mode", "full", metaObject);
        if(metaObject.hasGetter("format")) setFieldValByName("format", "node_tree", metaObject);
        if(metaObject.hasGetter("theme")) setFieldValByName("theme", "primary", metaObject);
        // ---------文件上传时间-------------
        if(metaObject.hasGetter("uploadTime")) setFieldValByName("uploadTime", new Date(), metaObject);
        // ---------操作时间-------------
        if(metaObject.hasGetter("operationDate")) setFieldValByName("operationDate", new Date(), metaObject);




//        this.strictInsertFill(metaObject, "createTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐使用)
//        this.strictUpdateFill(metaObject, "createTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
//        this.fillStrategy(metaObject, "createTime", LocalDateTime.now()); // 也可以使用(3.3.0 该方法有bug)
    }

    @Override
    public void updateFill(MetaObject metaObject) {
        if(metaObject.hasGetter("createTime")) setFieldValByName("createTime", new Date(), metaObject);
        if(metaObject.hasGetter("updateTime")) setFieldValByName("updateTime", new Date(), metaObject);
        if(metaObject.hasGetter("createUser")) setFieldValByName("createUser", "测试用户", metaObject);
        if(metaObject.hasGetter("updateUser")) setFieldValByName("updateUser", "测试用户", metaObject);

//        this.strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now()); // 起始版本 3.3.0(推荐)
//        this.strictUpdateFill(metaObject, "updateTime", () -> LocalDateTime.now(), LocalDateTime.class); // 起始版本 3.3.3(推荐)
//        this.fillStrategy(metaObject, "updateTime", LocalDateTime.now()); // 也可以使用(3.3.0 该方法有bug)
    }
}