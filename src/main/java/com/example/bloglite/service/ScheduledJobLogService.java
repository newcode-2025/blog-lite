package com.example.bloglite.service;

import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ScheduledJobLogDTO;
import com.example.bloglite.dto.ScheduledJobLogDeleteRequest;

import java.time.LocalDateTime;

/**
 * 定时任务执行日志服务
 */
public interface ScheduledJobLogService {

    PageResultDTO<ScheduledJobLogDTO> listLogs(
            Long jobId,
            String taskKey,
            Integer status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long current,
            long size
    );

    void deleteLogs(ScheduledJobLogDeleteRequest request);
}