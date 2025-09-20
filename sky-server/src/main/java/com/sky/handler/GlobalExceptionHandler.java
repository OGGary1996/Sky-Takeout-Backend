package com.sky.handler;

import com.sky.constant.MessageConstant;
import com.sky.exception.BaseException;
import com.sky.exception.DeletionNotAllowedException;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * 全局异常处理器，处理项目中抛出的业务异常
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    /**
     * 捕获业务异常
     * @param ex
     * @return
     */
    @ExceptionHandler
    public Result exceptionHandler(BaseException ex){
        log.error("异常信息：{}", ex.getMessage());
        return Result.error(ex.getMessage());
    }

    /*
    * 处理SQL异常，包括：
    *  处理新增员工时，username重复引发的DuplicateKeyException异常,等等
    * @param ex
    * @return
    * */
    @ExceptionHandler
    public Result exceptionHandler(SQLIntegrityConstraintViolationException ex){
        // 异常信息：Duplicate entry 'zhangsan' for key 'employee.idx_username'
        String message = ex.getMessage();
        log.error("Exception Message：{}", message);
        // 从异常信息中提取出重复的用户名
        if (message.contains("Duplicate entry")){
            String[] split = message.split(" ");
            String duplicateUsername = split[2];
            // 常规的异常信息在常量类MessageConstant中定义
            // 返回时调用异常信息
            return Result.error( duplicateUsername + MessageConstant.ALREADY_EXISTS);
        }
        // 其他SQL异常
        // TODO 可以继续扩展其他SQL异常的处理
        return Result.error(MessageConstant.UNKNOWN_ERROR);
    }

    /*
    * 处理删除分类时，关联了菜品或套餐引发的异常
    * @param ex
    * @return
    * */
    @ExceptionHandler
    public Result exceptionHandler(DeletionNotAllowedException ex){
        log.error("Exception Message：{}", ex.getMessage());
        // 直接返回异常信息
        // 异常信息手动抛出时定义
        return Result.error(ex.getMessage());
    }

}
