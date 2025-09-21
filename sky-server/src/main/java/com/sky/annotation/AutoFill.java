package com.sky.annotation;

import com.sky.enumeration.OperationType;

import java.lang.annotation.*;

@Target(ElementType.METHOD) // 表示该注解只能作用于方法上
@Retention(RetentionPolicy.RUNTIME) // 表示该注解在运行时仍然可用
@Documented // 表示该注解将包含在Javadoc中
public @interface AutoFill {
    // 定义字段operationType，类型为OperationType枚举
    // 用于指定数据库操作类型（插入或更新）
    OperationType value();
}
