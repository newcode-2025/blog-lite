package com.example.bloglite.service.impl;

import com.example.bloglite.dto.ScheduledJobLogDTO;
import com.example.bloglite.entity.ScheduledJob;
import com.example.bloglite.entity.ScheduledJobLog;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ScheduledJobLogMapper;
import com.example.bloglite.mapper.ScheduledJobMapper;
import com.example.bloglite.task.ScheduledTaskHandler;
import com.example.bloglite.task.ScheduledTaskRegistry;
import com.example.bloglite.task.TaskExecutionResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.Duration;
import java.time.LocalDateTime;

/**
 * 定时任务执行服务
 */
@Slf4j
@Service
public class ScheduledTaskExecutionService {

    private static final int STATUS_FAILED = 0;
    private static final int STATUS_SUCCESS = 1;
    private static final int MAX_LOG_LENGTH = 1000;
    private static final int MAX_EXCEPTION_LENGTH = 10000;
    private static final String TRIGGER_SCHEDULE = "SCHEDULE";
    private static final String TRIGGER_MANUAL = "MANUAL";

    private final ScheduledJobMapper scheduledJobMapper;
    private final ScheduledJobLogMapper scheduledJobLogMapper;
    private final ScheduledTaskRegistry taskRegistry;

    public ScheduledTaskExecutionService(
            ScheduledJobMapper scheduledJobMapper,
            ScheduledJobLogMapper scheduledJobLogMapper,
            ScheduledTaskRegistry taskRegistry
    ) {
        this.scheduledJobMapper = scheduledJobMapper;
        this.scheduledJobLogMapper = scheduledJobLogMapper;
        this.taskRegistry = taskRegistry;
    }

    /**
     * 执行指定任务并写入执行日志
     */
    public ScheduledJobLogDTO execute(Long jobId, String triggerType) {
        if (jobId == null || jobId < 1) {
            throw new BizException(40000, "任务 ID 不正确");
        }

        ScheduledJob scheduledJob = scheduledJobMapper.selectById(jobId);

        if (scheduledJob == null) {
            throw new BizException(40400, "定时任务不存在");
        }

        String normalizedTriggerType = TRIGGER_MANUAL.equalsIgnoreCase(
                triggerType
        )
                ? TRIGGER_MANUAL
                : TRIGGER_SCHEDULE;

        LocalDateTime startTime = LocalDateTime.now();
        ScheduledJobLog jobLog = createBaseLog(
                scheduledJob,
                normalizedTriggerType,
                startTime
        );
        String message = "";
        String exceptionInfo = null;

        try {
            ScheduledTaskHandler handler = taskRegistry.requireHandler(
                    scheduledJob.getTaskKey()
            );
            TaskExecutionResult result = handler.execute();

            if (result == null || !result.success()) {
                jobLog.setStatus(STATUS_FAILED);
                message = result == null
                        ? "任务执行失败"
                        : result.message();
            } else {
                jobLog.setStatus(STATUS_SUCCESS);
                message = result.message();
            }
        } catch (RuntimeException exception) {
            jobLog.setStatus(STATUS_FAILED);
            message = exception.getMessage();
            exceptionInfo = buildStackTrace(exception);
            log.error(
                    "定时任务执行失败，jobId=" + jobId
                            + "，taskKey=" + scheduledJob.getTaskKey(),
                    exception
            );
        }

        LocalDateTime endTime = LocalDateTime.now();
        jobLog.setJobMessage(truncate(message, MAX_LOG_LENGTH));
        jobLog.setExceptionInfo(exceptionInfo);
        jobLog.setEndTime(endTime);
        jobLog.setDurationMs(
                Duration.between(startTime, endTime).toMillis()
        );
        jobLog.setCreatedAt(endTime);
        scheduledJobLogMapper.insert(jobLog);
        return toDTO(jobLog);
    }

    private ScheduledJobLog createBaseLog(
            ScheduledJob scheduledJob,
            String triggerType,
            LocalDateTime startTime
    ) {
        ScheduledJobLog jobLog = new ScheduledJobLog();
        jobLog.setJobId(scheduledJob.getId());
        jobLog.setTaskKey(scheduledJob.getTaskKey());
        jobLog.setJobName(scheduledJob.getJobName());
        jobLog.setJobGroup(scheduledJob.getJobGroup());
        jobLog.setTriggerType(triggerType);
        jobLog.setStartTime(startTime);
        return jobLog;
    }

    private String buildStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return truncate(stringWriter.toString(), MAX_EXCEPTION_LENGTH);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength
                ? value
                : value.substring(0, maxLength);
    }

    private ScheduledJobLogDTO toDTO(ScheduledJobLog jobLog) {
        ScheduledJobLogDTO dto = new ScheduledJobLogDTO();
        dto.setId(jobLog.getId());
        dto.setJobId(jobLog.getJobId());
        dto.setTaskKey(jobLog.getTaskKey());
        dto.setJobName(jobLog.getJobName());
        dto.setJobGroup(jobLog.getJobGroup());
        dto.setTriggerType(jobLog.getTriggerType());
        dto.setStatus(jobLog.getStatus());
        dto.setJobMessage(jobLog.getJobMessage());
        dto.setExceptionInfo(jobLog.getExceptionInfo());
        dto.setStartTime(jobLog.getStartTime());
        dto.setEndTime(jobLog.getEndTime());
        dto.setDurationMs(jobLog.getDurationMs());
        dto.setCreatedAt(jobLog.getCreatedAt());
        return dto;
    }
}