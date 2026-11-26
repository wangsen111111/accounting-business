package com.ywjs.jcsy.common.handler.typehandler;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * 类型转换,在我们启用了我们自定义的这个TypeHandler之后，数据的读写都会被这个类所过滤
 * @author zhouwei
 * @create 2019-12-17 9:43
 */
// @MappedJdbcTypes定义的是JdbcType类型，这里的类型不可自己随意定义，必须要是枚举类org.apache.ibatis.type.JdbcType所枚举的数据类型。
@MappedJdbcTypes({JdbcType.VARCHAR})
// @MappedTypes定义的是JavaType的数据类型，描述了哪些Java类型可被拦截。
@MappedTypes({List.class})
public class ListTypeHandler extends BaseTypeHandler<List> {
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, List parameter, JdbcType jdbcType) throws SQLException {
        if(parameter == null){
            ps.setString(i,null);
        }else {
            String jsonString = JSON.toJSONString(parameter);
            ps.setString(i,jsonString);
        }
    }

    @Override
    public List getNullableResult(ResultSet rs, String columnName) throws SQLException {
        String string = rs.getString(columnName);
        List<String> list = JSON.parseObject(string, new TypeReference<List<String>>() { });
        return  list ;
    }

    @Override
    public List getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        String string = rs.getString(columnIndex);
        List<String> list = JSON.parseObject(string, new TypeReference<List<String>>() { });
        return  list ;
    }

    @Override
    public List getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        String string = cs.getString(columnIndex);
        List<String> list = JSON.parseObject(string, new TypeReference<List<String>>() { });
        return  list ;
    }
}
