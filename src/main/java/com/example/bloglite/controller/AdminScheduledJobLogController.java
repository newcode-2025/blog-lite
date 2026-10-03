package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ScheduledJobLogDTO;
import com.example.bloglite.dto.ScheduledJobLogDeleteRequest;
import com.example.bloglite.service.ScheduledJobLogService;
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
 * 后台定时任务日志接口
 */
@RestController
@RequestMapping("/api/v1/admin/job-logs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminScheduledJobLogController {

    private final ScheduledJobLogService scheduledJobLogService;

    public AdminScheduledJobLogController(
            ScheduledJobLogService scheduledJobLogService
    ) {
        this.scheduledJobLogService = scheduledJobLogService;
    }

    @GetMapping
    public ApiResponse<PageResultDTO<ScheduledJobLogDTO>> listLogs(
            @RequestParam(required = false) Long jobId,
            @RequestParam(defaultValue = "") String taskKey,
            @RequestParam(required = false) Integer status,
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
                scheduledJobLogService.listLogs(
                        jobId,
                        taskKey,
                        status,
                        startTime,
                        endTime,
                        current,
                        size
                )
        );
    }

    @DeleteMapping
    public ApiResponse<Void> deleteLogs(
            @Valid @RequestBody ScheduledJobLogDeleteRequest request
    ) {
        scheduledJobLogService.deleteLogs(request);
        return ApiResponse.success(null);
    }
}