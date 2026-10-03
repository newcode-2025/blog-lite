package com.example.bloglite.config;

import jakarta.validation.constraints.Min;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 定时任务和日志清理配置
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "blog.tasks")
public class TaskProperties {

    @Min(value = 1, message = "操作日志保留天数必须大于 0")
    private int operationLogRetentionDays;

    @Min(value = 1, message = "异常日志保留天数必须大于 0")
    private int exceptionLogRetentionDays;

    @Min(value = 1, message = "任务日志保留天数必须大于 0")
    private int jobLogRetentionDays;

    @Min(value = 1, message = "通知最大重试次数必须大于 0")
    private int notificationRetryMaxCount;

    @Min(value = 1, message = "通知重试批次大小必须大于 0")
    private int notificationRetryBatchSize;
}