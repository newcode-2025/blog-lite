package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 定时任务保存请求
 */
@Data
public class ScheduledJobSaveRequest {

    @NotBlank(message = "任务键不能为空")
    @Size(max = 100, message = "任务键不能超过 100 个字符")
    private String taskKey;

    @NotBlank(message = "任务名称不能为空")
    @Size(max = 100, message = "任务名称不能超过 100 个字符")
    private String jobName;

    @Size(max = 100, message = "任务分组不能超过 100 个字符")
    private String jobGroup;

    @NotBlank(message = "Cron 表达式不能为空")
    @Size(max = 100, message = "Cron 表达式不能超过 100 个字符")
    private String cronExpression;

    @NotNull(message = "任务状态不能为空")
    @Min(value = 0, message = "任务状态只能是 0 或 1")
    @Max(value = 1, message = "任务状态只能是 0 或 1")
    private Integer status;

    @NotNull(message = "并发设置不能为空")
    @Min(value = 0, message = "并发设置只能是 0 或 1")
    @Max(value = 1, message = "并发设置只能是 0 或 1")
    private Integer concurrent;

    @NotNull(message = "错失策略不能为空")
    @Min(value = 0, message = "错失策略必须在 0 到 3 之间")
    @Max(value = 3, message = "错失策略必须在 0 到 3 之间")
    private Integer misfirePolicy;

    @Size(max = 500, message = "任务说明不能超过 500 个字符")
    private String description;
}