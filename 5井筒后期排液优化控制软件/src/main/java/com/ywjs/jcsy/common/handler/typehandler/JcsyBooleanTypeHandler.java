package com.ywjs.jcsy.common.handler.typehandler;

import org.apache.ibatis.type.BaseTypeHandler;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;

import java.sql.*;

/**
 * 类型转换,在我们启用了我们自定义的这个TypeHandler之后，数据的读写都会被这个类所过滤
 * @author zhouwei
 * @create 2019-12-17 9:43
 */
// @MappedJdbcTypes定义的是JdbcType类型，这里的类型不可自己随意定义，必须要是枚举类org.apache.ibatis.type.JdbcType所枚举的数据类型。
@MappedJdbcTypes({JdbcType.VARCHAR})
// @MappedTypes定义的是JavaType的数据类型，描述了哪些Java类型可被拦截。
@MappedTypes({Boolean.class})
public class JcsyBooleanTypeHandler extends BaseTypeHandler<Boolean> {
    // 在setNonNullParameter方法中，我们重新定义要写往数据库的数据。
    @Override
    public void setNonNullParameter(PreparedStatement ps, int i, Boolean parameter, JdbcType jdbcType) throws SQLException {
        if(parameter) {
            ps.setInt(i, 1);
        } else {
            ps.setInt(i, 0);
        }
    }

    //在另外三个方法中我们将从数据库读出的数据类型进行转换。

    @Override
    public Boolean getNullableResult(ResultSet rs, String columnName) throws SQLException {
        return  rs.getInt(columnName) == 1 ? true : false;
    }

    @Override
    public Boolean getNullableResult(ResultSet rs, int columnIndex) throws SQLException {
        return  rs.getInt(columnIndex) == 1 ? true : false;
    }

    @Override
    public Boolean getNullableResult(CallableStatement cs, int columnIndex) throws SQLException {
        return  cs.getInt(columnIndex) == 1 ? true : false;
    }
}
