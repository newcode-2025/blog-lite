package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.OperationLogDTO;
import com.example.bloglite.dto.OperationLogDeleteRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.OperationLogService;
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
 * 后台操作日志接口
 */
@RestController
@RequestMapping("/api/v1/admin/operation-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminOperationLogController {

    private final OperationLogService operationLogService;

    public AdminOperationLogController(
            OperationLogService operationLogService
    ) {
        this.operationLogService = operationLogService;
    }

    /**
     * 分页查询操作日志
     */
    @GetMapping
    public ApiResponse<PageResultDTO<OperationLogDTO>> listLogs(
            @RequestParam(defaultValue = "") String module,
            @RequestParam(defaultValue = "") String action,
            @RequestParam(defaultValue = "") String operatorName,
            @RequestParam(required = false) Integer success,
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
                operationLogService.listLogs(
                        module,
                        action,
                        operatorName,
                        success,
                        startTime,
                        endTime,
                        current,
                        size
                )
        );
    }

    /**
     * 批量删除操作日志
     */
    @DeleteMapping
    public ApiResponse<Void> deleteLogs(
            @Valid @RequestBody OperationLogDeleteRequest request
    ) {
        operationLogService.deleteLogs(request);
        return ApiResponse.success(null);
    }
}