package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 系统异常日志返回结果
 */
@Data
public class ExceptionLogDTO {

    private Long id;

    private String requestMethod;

    private String requestUri;

    private String handlerMethod;

    private String requestParams;

    private String exceptionType;

    private String exceptionMessage;

    private String stackTrace;

    private Long operatorId;

    private String operatorName;

    private String ipAddress;

    private LocalDateTime createdAt;
}