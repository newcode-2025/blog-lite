package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统异常日志实体
 */
@Data
@TableName("exception_log")
public class ExceptionLog {

    /**
     * 日志 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * HTTP 请求方法
     */
    private String requestMethod;

    /**
     * 请求路径
     */
    private String requestUri;

    /**
     * 控制器处理方法
     */
    private String handlerMethod;

    /**
     * 脱敏并截断后的请求参数
     */
    private String requestParams;

    /**
     * 异常类型
     */
    private String exceptionType;

    /**
     * 异常消息
     */
    private String exceptionMessage;

    /**
     * 异常堆栈
     */
    private String stackTrace;

    /**
     * 当前用户 ID
     */
    private Long operatorId;

    /**
     * 当前用户名称
     */
    private String operatorName;

    /**
     * 请求 IP
     */
    private String ipAddress;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
