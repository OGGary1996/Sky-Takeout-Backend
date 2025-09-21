package com.sky.aspect;

import com.sky.annotation.AutoFill;
import com.sky.constant.AutoFillConstant;
import com.sky.context.BaseContext;
import com.sky.enumeration.OperationType;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.stereotype.Component;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;

/*
* 执行insert和update操作时，自动填充公共字段的切面类
* updateTime updateUser createTime createUser
* */
@Aspect
@Component
@Slf4j
public class AutoFillAspect {
    /*
    * 定义切入点
    * */
    @Pointcut("execution( * com.sky.mapper..*(..)) && @annotation(com.sky.annotation.AutoFill)")
    public void autoFillPointCut(){};


    /*
    * 定义通知
    * 本通知定义为@Before通知
    * 在执行方法之前，执行该通知
    * */
    @Before("autoFillPointCut()")
    public void autoFill(JoinPoint joinPoint) throws InvocationTargetException, NoSuchMethodException, IllegalAccessException {
        log.info("autoFill called...");
        // 1. 获取当前方法的@AutoFill的参数（insert/update）
            // 通过反射获取到方法对象，然后获取注解对象，最后获取注解的参数值
        MethodSignature signature = (MethodSignature)joinPoint.getSignature();
        Method method = signature.getMethod();
        AutoFill annotation = method.getAnnotation(AutoFill.class);
        OperationType operationType = annotation.value();

        // 2. 获取当前方法的参数（Entity 对象），因为本质上是为了给 Entity 对象的公共字段赋值
        // 约定，第一个参数就是 Entity 对象
        if (joinPoint.getArgs() == null || joinPoint.getArgs().length == 0){
            // 防止空指针异常
            log.warn("The method has no parameters, cannot perform auto fill...");
            return;
        }
        Object entity = joinPoint.getArgs()[0];

        // 3. 准备数据
        LocalDateTime now = LocalDateTime.now();
        Long currentUserId = BaseContext.getCurrentId();

        // 4. 根据不同的操作类型，给 Entity 对象的公共字段赋值
        if (operationType == OperationType.INSERT){
            log.info("Insert operation, auto fill...");
            setFieldValue(entity, AutoFillConstant.SET_CREATE_TIME,LocalDateTime.class,now);
            setFieldValue(entity, AutoFillConstant.SET_CREATE_USER,Long.class,currentUserId);
            setFieldValue(entity, AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class,now);
            setFieldValue(entity, AutoFillConstant.SET_UPDATE_USER,Long.class,currentUserId);
        }else if (operationType == OperationType.UPDATE){
            log.info("Update operation, auto fill...");
            setFieldValue(entity, AutoFillConstant.SET_UPDATE_TIME,LocalDateTime.class,now);
            setFieldValue(entity, AutoFillConstant.SET_UPDATE_USER,Long.class,currentUserId);
        }
    }

    /*
    * 赋值的具体方法
    * 1. 方法参数：entity对象，方法的名称，赋值的数据Class对象，赋值的数据
    * 2. 通过反射获取到entity方法的set方法对象，然后调用invoke方法，进行赋值
    * 3. 注意：set方法的名称是有规律的，为了防止写错，采用常量类AutoFillConstant类
    * */
    public void setFieldValue(Object entity,String methodName,Class<?> paramType, Object paramValue) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException {
        Method method = entity.getClass().getMethod(methodName, paramType);
        method.invoke(entity, paramValue);
    }
}
