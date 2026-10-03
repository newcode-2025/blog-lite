package com.example.bloglite.task;

import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 系统心跳任务，用于验证调度器运行状态
 */
@Component
public class SystemHeartbeatTaskHandler implements ScheduledTaskHandler {

    private static final DateTimeFormatter DATE_TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public String getTaskKey() {
        return "systemHeartbeat";
    }

    @Override
    public String getTaskName() {
        return "系统心跳";
    }

    @Override
    public TaskExecutionResult execute() {
        return TaskExecutionResult.success(
                "系统心跳正常，执行时间="
                        + LocalDateTime.now().format(DATE_TIME_FORMATTER)
        );
    }
}