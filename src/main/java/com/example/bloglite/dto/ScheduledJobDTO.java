package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务返回结果
 */
@Data
public class ScheduledJobDTO {

    private Long id;

    private String taskKey;

    private String jobName;

    private String jobGroup;

    private String cronExpression;

    private Integer status;

    private Integer concurrent;

    private Integer misfirePolicy;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}