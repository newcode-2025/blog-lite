package com.example.bloglite.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.config.TaskProperties;
import com.example.bloglite.entity.OperationLog;
import com.example.bloglite.mapper.OperationLogMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 操作日志清理任务
 */
@Component
public class OperationLogCleanupTaskHandler implements ScheduledTaskHandler {

    private final OperationLogMapper operationLogMapper;
    private final TaskProperties taskProperties;

    public OperationLogCleanupTaskHandler(
            OperationLogMapper operationLogMapper,
            TaskProperties taskProperties
    ) {
        this.operationLogMapper = operationLogMapper;
        this.taskProperties = taskProperties;
    }

    @Override
    public String getTaskKey() {
        return "operationLogCleanup";
    }

    @Override
    public String getTaskName() {
        return "操作日志清理";
    }

    @Override
    public TaskExecutionResult execute() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(
                taskProperties.getOperationLogRetentionDays()
        );
        int deleted = operationLogMapper.delete(
                Wrappers.<OperationLog>lambdaQuery()
                        .lt(OperationLog::getCreatedAt, cutoff)
        );
        return TaskExecutionResult.success(
                "清理操作日志 " + deleted + " 条"
        );
    }
}