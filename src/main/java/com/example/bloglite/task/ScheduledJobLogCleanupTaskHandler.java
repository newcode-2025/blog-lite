package com.example.bloglite.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.config.TaskProperties;
import com.example.bloglite.entity.ScheduledJobLog;
import com.example.bloglite.mapper.ScheduledJobLogMapper;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 定时任务执行日志清理任务
 */
@Component
public class ScheduledJobLogCleanupTaskHandler
        implements ScheduledTaskHandler {

    private final ScheduledJobLogMapper scheduledJobLogMapper;
    private final TaskProperties taskProperties;

    public ScheduledJobLogCleanupTaskHandler(
            ScheduledJobLogMapper scheduledJobLogMapper,
            TaskProperties taskProperties
    ) {
        this.scheduledJobLogMapper = scheduledJobLogMapper;
        this.taskProperties = taskProperties;
    }

    @Override
    public String getTaskKey() {
        return "scheduledJobLogCleanup";
    }

    @Override
    public String getTaskName() {
        return "任务日志清理";
    }

    @Override
    public TaskExecutionResult execute() {
        LocalDateTime cutoff = LocalDateTime.now().minusDays(
                taskProperties.getJobLogRetentionDays()
        );
        int deleted = scheduledJobLogMapper.delete(
                Wrappers.<ScheduledJobLog>lambdaQuery()
                        .lt(ScheduledJobLog::getCreatedAt, cutoff)
        );
        return TaskExecutionResult.success(
                "清理任务日志 " + deleted + " 条"
        );
    }
}