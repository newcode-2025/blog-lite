package com.example.bloglite.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.config.TaskProperties;
import com.example.bloglite.entity.ExceptionLog;
import com.example.bloglite.mapper.ExceptionLogMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 异常日志清理任务
 */
@Component
public class ExceptionLogCleanupTaskHandler implements ScheduledTaskHandler {

    private final ExceptionLogMapper exceptionLogMapper;
    private final TaskProperties taskProperties;

    public ExceptionLogCleanupTaskHandler(
            ExceptionLogMapper exceptionLogMapper,
            TaskProperties taskProperties
    ) {
        this.exceptionLogMapper = exceptionLogMapper;
        this.taskProperties = taskProperties;
    }

    @Override
    public String getTaskKey() {
        return "exceptionLogCleanup";
    }

    @Override
    public String getTaskName() {
        return "异常日志清理";
    }

    @Override
    public TaskExecutionResult execute() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(
                taskProperties.getExceptionLogRetentionDays()
        );
        int deleted = exceptionLogMapper.delete(
                Wrappers.<ExceptionLog>lambdaQuery()
                        .lt(ExceptionLog::getCreatedAt, cutoff)
        );
        return TaskExecutionResult.success(
                "清理异常日志 " + deleted + " 条"
        );
    }
}