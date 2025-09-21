package com.sky.enumeration;

/**
 * 数据库操作类型
 */
public enum OperationType {
    /*
    * 指定数据库操作类型的枚举类
    * 用途：
    * 在AOP实现字段自动填充时（create_time/user, update_time/user），区分是插入操作还是更新操作
    * 插入操作需要填充四个字段，而更新操作只需要填充两个字段
    * */

    /**
     * 更新操作
     */
    UPDATE,

    /**
     * 插入操作
     */
    INSERT
}
