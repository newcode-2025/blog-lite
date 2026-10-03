package com.example.bloglite.dto;

import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 批量删除任务执行日志请求
 */
@Data
public class ScheduledJobLogDeleteRequest {

    @NotEmpty(message = "任务日志 ID 列表不能为空")
    @Size(max = 100, message = "一次最多删除 100 条任务日志")
    private List<@Positive(message = "任务日志 ID 必须大于 0") Long> ids;
}