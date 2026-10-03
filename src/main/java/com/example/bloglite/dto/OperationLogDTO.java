package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 操作日志返回结果
 */
@Data
public class OperationLogDTO {

    private Long id;

    private String module;

    private String operationAction;

    private String description;

    private String requestMethod;

    private String requestUri;

    private String handlerMethod;

    private String requestParams;

    private Integer responseCode;

    private Integer success;

    private Long operatorId;

    private String operatorName;

    private String ipAddress;

    private Long durationMs;

    private String errorMessage;

    private LocalDateTime createdAt;
}