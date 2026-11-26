package com.ywjs.jcsy.common.aspect;


import com.alibaba.fastjson.JSON;
import org.apache.ibatis.executor.statement.StatementHandler;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.plugin.*;
import org.apache.ibatis.session.ResultHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.sql.Statement;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author zhouwei
 * @create 2020-09-09 9:15
 */
@Intercepts({@Signature(type = StatementHandler.class, method = "query", args = {Statement.class, ResultHandler.class}),
        @Signature(type = StatementHandler.class, method = "update", args = {Statement.class}),
        @Signature(type = StatementHandler.class, method = "batch", args = {Statement.class})})
@Component
public class SqlStatementInterceptor implements Interceptor {

    private static final Logger LOGGER = LoggerFactory.getLogger(SqlStatementInterceptor.class);

    private static Set<String> list = new HashSet<>();
    public static ThreadLocal<Set<String>> threadLocal = new ThreadLocal<>();





    @Override
    public Object intercept(Invocation invocation) {
        // 开始时间
        try {
//            org.apache.ibatis.session.Configuration configuration = sqlSessionFactory.getConfiguration();
            Object target = invocation.getTarget();
//            System.out.println("-->"+ JSONObject.toJSONString(target));
            Field delegate = target.getClass().getDeclaredField("delegate");
            delegate.setAccessible(true);
            Object delegateObj = delegate.get(target);
            Field parameterHandler = delegateObj.getClass().getSuperclass().getDeclaredField("boundSql");
            parameterHandler.setAccessible(true);
            Object parameterHandlerObj = parameterHandler.get(delegateObj);
            BoundSql boundSql = (BoundSql)parameterHandlerObj;
//            String s = SqlUtils.showSql(configuration, boundSql);


            Field parameterObject = parameterHandlerObj.getClass().getDeclaredField("parameterObject");
            parameterObject.setAccessible(true);
            Object o = parameterObject.get(parameterHandlerObj);
            System.out.println("拦截到执行的sql:--->"+o.toString());
            Field sqlField = parameterHandlerObj.getClass().getDeclaredField("sql");
            sqlField.setAccessible(true);
            Object o1 = sqlField.get(parameterHandlerObj);
            String sqlStr = o1.toString().replace("\n", " ");
            System.out.println("sql:--->"+sqlStr);
            if (sqlStr.contains("updata") || sqlStr.contains("UPDATA") ){
                String jsonString = JSON.toJSONString(boundSql);
                System.out.println(jsonString);
                System.out.println(boundSql.toString());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        long start = System.currentTimeMillis();
        invocation.getArgs();
        try {
            return invocation.proceed();
        } catch (Exception e) {
            LOGGER.error("执行失败！", e);
            return null;
        } finally {
            long end = System.currentTimeMillis();
            long time = end - start;
            LOGGER.info("cost time {}ms", time);
        }
    }

    @Override
    public Object plugin(Object arg0) {
        return Plugin.wrap(arg0, new SqlStatementInterceptor());
    }

    @Override
    public void setProperties(Properties arg0) {
    }
}
