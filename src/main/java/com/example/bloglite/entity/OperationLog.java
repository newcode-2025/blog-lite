package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台操作日志实体
 */
@Data
@TableName("operation_log")
public class OperationLog {

    /**
     * 日志 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作动作
     */
    private String operationAction;

    /**
     * 操作描述
     */
    private String description;

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
     * 业务响应码
     */
    private Integer responseCode;

    /**
     * 操作是否成功：0 失败，1 成功
     */
    private Integer success;

    /**
     * 操作人 ID
     */
    private Long operatorId;

    /**
     * 操作人名称
     */
    private String operatorName;

    /**
     * 操作 IP
     */
    private String ipAddress;

    /**
     * 执行耗时，单位毫秒
     */
    private Long durationMs;

    /**
     * 失败原因
     */
    private String errorMessage;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
