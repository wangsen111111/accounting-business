//package com.ywjs.jcsy.common.handler.typehandler;
//
//import cn.hutool.core.util.StrUtil;
//import com.alibaba.fastjson.JSON;
//import com.alibaba.fastjson.JSONObject;
//import com.ywjs.jcsy.entity.File;
//import org.apache.ibatis.type.JdbcType;
//import org.apache.ibatis.type.MappedJdbcTypes;
//import org.apache.ibatis.type.MappedTypes;
//import org.apache.ibatis.type.TypeHandler;
//
//import java.sql.CallableStatement;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.stream.Collectors;
//
///**
// * @author zhouwei
// * @create 2020-08-23 12:06
// */
//
////支持的java对象
//@MappedTypes(value = {List.class})
//@MappedJdbcTypes({JdbcType.VARCHAR})
//public class MountLocTypeHandler<T extends Object> implements TypeHandler<List<File.MountLoc>> {
//
//    private List<File.MountLoc> getMapJsonStr(String content) {
//        if (StrUtil.isEmpty(content)) {
//            return new ArrayList<>();
//        }else {
//            List<File.MountLoc> list = (List<File.MountLoc>) JSON.parseObject(content, List.class).stream().map(item ->
//                 JSON.parseObject(item.toString(), File.MountLoc.class)
//            ).collect(Collectors.toList());
//            return list;
//        }
//
//
//    }
//
//    /**
//     * 用于定义在Mybatis设置参数时该如何把Java类型的参数转换为对应的数据库类型
//     *
//     * <PRE>
//     * PreparedStatement pstmt = con.prepareStatement("UPDATE EMPLOYEES
//     * SET SALARY = ? WHERE ID = ?");
//     * pstmt.setBigDecimal(1, 153833.00)
//     * pstmt.setInt(2, 110592)
//     * </PRE>
//     *
//     * @param preparedStatement An object that represents a precompiled SQL statement
//     * @param i                 当前参数的位置
//     * @param t           当前参数的Java对象
//     * @param jdbcType          当前参数的数据库类型
//     * @throws SQLException
//     */
//    @Override
//    public void setParameter(PreparedStatement preparedStatement, int i, List<File.MountLoc> t, JdbcType jdbcType) throws SQLException {
//        if (t == null) {
//            preparedStatement.setString(i, null);
//        } else {
//            preparedStatement.setString(i, JSON.toJSONString(t));
//        }
//    }
//
//    @Override
//    public List<File.MountLoc> getResult(ResultSet resultSet, String s) throws SQLException {
//        return getMapJsonStr(resultSet.getString(s));
//    }
//
//    @Override
//    public List<File.MountLoc> getResult(ResultSet resultSet, int i) throws SQLException {
//        return getMapJsonStr(resultSet.getString(i));
//    }
//
//    @Override
//    public List<File.MountLoc> getResult(CallableStatement callableStatement, int i) throws SQLException {
//        return getMapJsonStr(callableStatement.getString(i));
//    }
//
//}
