package com.ywjs.jcsy.aspect;

import cn.hutool.core.date.DateUtil;
import com.ywjs.jcsy.exception.GlobalException;
import com.ywjs.jcsy.utils.HttpClientUtil;
import com.ywjs.jcsy.utils.PrintUtil;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.security.Permissions;
import java.util.*;

/**
 * 日志文件记录
 *
 * @ClassName WebLogAspect
 * @Description TODO(日志文件记录)
 * @Author 周伟
 * @Date 2019年12月11日 上午11:29:21
 */
@Aspect
@Component
@Slf4j
public class WebLogAspect {

    @Autowired
    HttpServletRequest request;
    /*
     * 切点规则，两个..代表所有子目录，最后括号里的两个..代表所有参数
     * 1、execution(): 表达式主体。
     * 2、第一个*号：表示返回类型，*号表示所有的类型。
     * 3、包名：表示需要拦截的包名，后面的两个句点表示当前包和当前包的所有子包，com.sample.service.impl包、子孙包下所有类的方法。
     * 4、第二个*号：表示类名，*号表示所有的类。
     * 5、*(..):最后这个星号表示方法名，*号表示所有的方法，后面括弧里面表示方法的参数，两个句点表示任何参数。
     */
    @Pointcut("execution( * com.ywjs.jcsy.controller.*.*(..))")
    public void logPointCut() {
    }

    @Around("logPointCut()")
    public Object doAround(ProceedingJoinPoint pjp) throws Throwable {
//        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
//        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
//        HttpServletRequest request = sra.getRequest();
        String ip = request.getRequestURL().toString();
        long startTime = System.currentTimeMillis();
        MethodSignature signature = (MethodSignature) pjp.getSignature();
        // 获取连接点方法
        Method method = signature.getMethod();
        Object ob = pjp.proceed();// ob 为方法的返回值
        long time = System.currentTimeMillis() - startTime;
        log.info("耗时 : " + time + " 毫秒");
        return ob;
    }


    @AfterThrowing(value = "logPointCut()", throwing = "e")
    public void afterThrowable(JoinPoint jp, Throwable e) {
        String time = DateUtil.format(new Date(),"yyyy-MM-dd HH:mm:ss");

        RequestAttributes ra = RequestContextHolder.getRequestAttributes();
        ServletRequestAttributes sra = (ServletRequestAttributes) ra;
        HttpServletRequest request = sra.getRequest();

//        HttpServletRequest request = HttpClientUtil.getHttpServletRequest();
        String ip = request.getRequestURL().toString();
        String requestMode = request.getMethod();
        // signature签名，获取连接点方法签名
        MethodSignature signature = (MethodSignature) jp.getSignature();
        // 返回该元素的指定类型的注解
        Class<?> aClass = jp.getTarget().getClass();
        // signature签名，获取连接点方法签名
        String methodName = jp.getSignature().getName(); // 方法名
        // 将异常信息封装到实体中
        ErrorLog errorLog = new ErrorLog();
        errorLog.setIp(ip).setRequestMode(requestMode).setPerformClass(aClass + "")
                .setMethodName(methodName).setErrorType(e.getMessage())
                .setErrorDescribe(PrintUtil.printStackTrace(e)).setTime(time);
        log.error(errorLog.getErrorLogInfo());
    }



}
