package com.example.bloglite.service;

import com.example.bloglite.dto.ExceptionLogDTO;
import com.example.bloglite.dto.ExceptionLogDeleteRequest;
import com.example.bloglite.dto.PageResultDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.time.LocalDateTime;

/**
 * 系统异常日志服务
 */
public interface ExceptionLogService {

    /**
     * 记录系统异常
     */
    void recordException(HttpServletRequest request, Throwable throwable);

    /**
     * 分页查询系统异常日志
     */
    PageResultDTO<ExceptionLogDTO> listLogs(
            String exceptionType,
            String requestUri,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long current,
            long size
    );

    /**
     * 批量删除系统异常日志
     */
    void deleteLogs(ExceptionLogDeleteRequest request);
}