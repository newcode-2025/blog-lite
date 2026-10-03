package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务执行日志返回结果
 */
@Data
public class ScheduledJobLogDTO {

    private Long id;

    private Long jobId;

    private String taskKey;

    private String jobName;

    private String jobGroup;

    private String triggerType;

    private Integer status;

    private String jobMessage;

    private String exceptionInfo;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private Long durationMs;

    private LocalDateTime createdAt;
}