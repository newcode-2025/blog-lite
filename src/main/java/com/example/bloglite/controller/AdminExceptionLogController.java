package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.ExceptionLogDTO;
import com.example.bloglite.dto.ExceptionLogDeleteRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.ExceptionLogService;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

/**
 * 后台系统异常日志接口
 */
@RestController
@RequestMapping("/api/v1/admin/exception-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminExceptionLogController {

    private final ExceptionLogService exceptionLogService;

    public AdminExceptionLogController(
            ExceptionLogService exceptionLogService
    ) {
        this.exceptionLogService = exceptionLogService;
    }

    /**
     * 分页查询异常日志
     */
    @GetMapping
    public ApiResponse<PageResultDTO<ExceptionLogDTO>> listLogs(
            @RequestParam(defaultValue = "") String exceptionType,
            @RequestParam(defaultValue = "") String requestUri,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime startTime,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime endTime,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                exceptionLogService.listLogs(
                        exceptionType,
                        requestUri,
                        startTime,
                        endTime,
                        current,
                        size
                )
        );
    }

    /**
     * 批量删除异常日志
     */
    @DeleteMapping
    public ApiResponse<Void> deleteLogs(
            @Valid @RequestBody ExceptionLogDeleteRequest request
    ) {
        exceptionLogService.deleteLogs(request);
        return ApiResponse.success(null);
    }
}