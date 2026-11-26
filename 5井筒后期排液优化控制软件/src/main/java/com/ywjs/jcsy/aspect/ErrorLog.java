package com.ywjs.jcsy.aspect;

import lombok.Data;
import lombok.experimental.Accessors;

/**
 * 错误日志实体
 *
 * @author zhouwei
 * @create 2019-12-18 14:15
 */
@Data
@Accessors(chain = true) // 开启链式操作
public class ErrorLog {
    private String ip; // 请求地址
    private String requestMode; // 请求方式
    private String performClass; // 执行类
    private String methodName; // 执行方法名
    private String errorType; // 异常类型
    private String errorDescribe; // 异常消息描述
    private String operation; // 操作描述
    private String time; // 异常发生时间

    public String getErrorLogInfo() {
        StringBuffer sb = new StringBuffer();
        sb.append("\r\n")
                .append(" 异常发生时间：").append(getTime()).append("\r\n")
                .append("请求地址：").append(getIp()).append("\r\n")
                .append("请求方式：").append(getRequestMode()).append("\r\n")
                .append("执行方法名：").append(getMethodName()).append("\r\n")
                .append("执行方法所在类：").append(getPerformClass()).append("\r\n")
//                .append("执行操作：").append(getOperation()).append("\r\n")
//                .append("异常类型：").append(getErrorType()).append("\r\n")
                .append("异常描述：").append(getErrorDescribe()).append("\r\n");
        return sb.toString();
    }
}
