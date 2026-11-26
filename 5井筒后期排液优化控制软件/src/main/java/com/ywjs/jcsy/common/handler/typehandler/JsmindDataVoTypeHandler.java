package com.ywjs.jcsy.common.handler.typehandler;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSON;
import com.ywjs.jcsy.vo.JsmindDataVo;
import org.apache.ibatis.type.JdbcType;
import org.apache.ibatis.type.MappedJdbcTypes;
import org.apache.ibatis.type.MappedTypes;
import org.apache.ibatis.type.TypeHandler;

import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author zhouwei
 * @create 2020-08-23 12:06
 */

//支持的java对象
@MappedTypes(value = {JsmindDataVo.class})
@MappedJdbcTypes({JdbcType.VARCHAR})
public class JsmindDataVoTypeHandler <T extends Object> implements TypeHandler<JsmindDataVo> {

    private JsmindDataVo getJsmindDataVoJsonStr(String content) {
        if (StrUtil.isEmpty(content)) {
            return new JsmindDataVo();
        }
        JsmindDataVo jsmindDataVo = JSON.parseObject(content, JsmindDataVo.class);
        return jsmindDataVo;

    }

    /**
     * 用于定义在Mybatis设置参数时该如何把Java类型的参数转换为对应的数据库类型
     *
     * <PRE>
     * PreparedStatement pstmt = con.prepareStatement("UPDATE EMPLOYEES
     * SET SALARY = ? WHERE ID = ?");
     * pstmt.setBigDecimal(1, 153833.00)
     * pstmt.setInt(2, 110592)
     * </PRE>
     *
     * @param preparedStatement An object that represents a precompiled SQL statement
     * @param i                 当前参数的位置
     * @param t           当前参数的Java对象
     * @param jdbcType          当前参数的数据库类型
     * @throws SQLException
     */
    @Override
    public void setParameter(PreparedStatement preparedStatement, int i, JsmindDataVo t, JdbcType jdbcType) throws SQLException {
        if (t == null) {
            preparedStatement.setString(i, null);
        } else {
            preparedStatement.setString(i, JSON.toJSONString(t));
        }
    }

    @Override
    public JsmindDataVo getResult(ResultSet resultSet, String s) throws SQLException {
        return getJsmindDataVoJsonStr(resultSet.getString(s));
    }

    @Override
    public JsmindDataVo getResult(ResultSet resultSet, int i) throws SQLException {
        return getJsmindDataVoJsonStr(resultSet.getString(i));
    }

    @Override
    public JsmindDataVo getResult(CallableStatement callableStatement, int i) throws SQLException {
        return getJsmindDataVoJsonStr(callableStatement.getString(i));
    }

}
