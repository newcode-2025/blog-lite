package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ScheduledJobDTO;
import com.example.bloglite.dto.ScheduledJobLogDTO;
import com.example.bloglite.dto.ScheduledJobSaveRequest;
import com.example.bloglite.dto.ScheduledJobStatusUpdateRequest;
import com.example.bloglite.dto.TaskHandlerOptionDTO;
import com.example.bloglite.entity.ScheduledJob;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ScheduledJobMapper;
import com.example.bloglite.quartz.ConcurrentScheduledTaskQuartzJob;
import com.example.bloglite.quartz.DisallowConcurrentScheduledTaskQuartzJob;
import com.example.bloglite.service.ScheduledJobService;
import com.example.bloglite.task.ScheduledTaskHandler;
import com.example.bloglite.task.ScheduledTaskRegistry;
import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.CronTrigger;
import org.quartz.Job;
import org.quartz.JobBuilder;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.TriggerBuilder;
import org.quartz.TriggerKey;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务管理服务实现
 */
@Slf4j
@Service
public class ScheduledJobServiceImpl implements ScheduledJobService {

    private static final String TASK_CLASS_NAME = "SCHEDULED_TASK_";
    private static final String DEFAULT_GROUP = "DEFAULT";
    private static final int STATUS_PAUSED = 0;
    private static final int STATUS_RUNNING = 1;
    private static final int CONCURRENT_YES = 1;
    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private final ScheduledJobMapper scheduledJobMapper;
    private final ScheduledTaskRegistry taskRegistry;
    private final ScheduledTaskExecutionService executionService;
    private final Scheduler scheduler;

    public ScheduledJobServiceImpl(
            ScheduledJobMapper scheduledJobMapper,
            ScheduledTaskRegistry taskRegistry,
            ScheduledTaskExecutionService executionService,
            Scheduler scheduler
    ) {
        this.scheduledJobMapper = scheduledJobMapper;
        this.taskRegistry = taskRegistry;
        this.executionService = executionService;
        this.scheduler = scheduler;
    }

    @Override
    public PageResultDTO<ScheduledJobDTO> listJobs(
            Integer status,
            String keywords,
            long current,
            long size
    ) {
        validateStatus(status, true);

        long normalizedCurrent = current < 1 ? 1 : current;
        long normalizedSize = size < 1
                ? DEFAULT_SIZE
                : Math.min(size, MAX_SIZE);
        long offset = (normalizedCurrent - 1) * normalizedSize;
        String normalizedKeywords = normalizeText(keywords);

        Long count = scheduledJobMapper.selectCount(
                buildListWrapper(status, normalizedKeywords)
        );
        List<ScheduledJobDTO> records = scheduledJobMapper.selectList(
                        buildListWrapper(status, normalizedKeywords)
                                .orderByDesc(ScheduledJob::getId)
                                .last("LIMIT " + offset + "," + normalizedSize)
                )
                .stream()
                .map(this::toDTO)
                .toList();

        return new PageResultDTO<>(
                records,
                count,
                normalizedCurrent,
                normalizedSize
        );
    }

    @Override
    public List<TaskHandlerOptionDTO> listHandlers() {
        return taskRegistry.listHandlers()
                .stream()
                .map(handler -> new TaskHandlerOptionDTO(
                        handler.getTaskKey(),
                        handler.getTaskName()
                ))
                .toList();
    }

    @Override
    public ScheduledJobDTO getJob(Long jobId) {
        return toDTO(requireJob(jobId));
    }

    @Override
    public ScheduledJobDTO createJob(ScheduledJobSaveRequest request) {
        validateSaveRequest(request);

        ScheduledJob scheduledJob = new ScheduledJob();
        applyRequest(scheduledJob, request);

        LocalDateTime now = LocalDateTime.now();
        scheduledJob.setCreatedAt(now);
        scheduledJob.setUpdatedAt(now);

        try {
            scheduledJobMapper.insert(scheduledJob);
        } catch (DuplicateKeyException exception) {
            throw new BizException(40900, "该任务键已存在");
        }

        try {
            scheduleJob(scheduledJob);
        } catch (RuntimeException exception) {
            scheduledJobMapper.deleteById(scheduledJob.getId());
            throw exception;
        }

        return toDTO(requireJob(scheduledJob.getId()));
    }

    @Override
    public ScheduledJobDTO updateJob(
            Long jobId,
            ScheduledJobSaveRequest request
    ) {
        ScheduledJob oldJob = requireJob(jobId);
        validateSaveRequest(request);

        ScheduledJob updatedJob = new ScheduledJob();
        updatedJob.setId(oldJob.getId());
        applyRequest(updatedJob, request);
        updatedJob.setCreatedAt(oldJob.getCreatedAt());
        updatedJob.setUpdatedAt(LocalDateTime.now());

        try {
            scheduledJobMapper.updateById(updatedJob);
        } catch (DuplicateKeyException exception) {
            throw new BizException(40900, "该任务键已存在");
        }

        try {
            scheduleJob(updatedJob);
        } catch (RuntimeException exception) {
            scheduledJobMapper.updateById(oldJob);
            try {
                scheduleJob(oldJob);
            } catch (RuntimeException restoreException) {
                log.error("恢复旧定时任务失败，jobId=" + jobId, restoreException);
            }
            throw exception;
        }

        return toDTO(requireJob(jobId));
    }

    @Override
    public ScheduledJobDTO updateStatus(
            Long jobId,
            ScheduledJobStatusUpdateRequest request
    ) {
        if (request == null) {
            throw new BizException(40000, "任务状态请求不能为空");
        }

        validateStatus(request.getStatus(), false);

        ScheduledJob job = requireJob(jobId);
        Integer oldStatus = job.getStatus();

        if (oldStatus.equals(request.getStatus())) {
            return toDTO(job);
        }

        job.setStatus(request.getStatus());
        job.setUpdatedAt(LocalDateTime.now());
        scheduledJobMapper.updateById(job);

        try {
            changeQuartzStatus(job);
        } catch (RuntimeException exception) {
            job.setStatus(oldStatus);
            scheduledJobMapper.updateById(job);
            throw exception;
        }

        return toDTO(requireJob(jobId));
    }

    @Override
    public ScheduledJobLogDTO runOnce(Long jobId) {
        requireJob(jobId);
        return executionService.execute(jobId, "MANUAL");
    }

    @Override
    public void deleteJob(Long jobId) {
        ScheduledJob job = requireJob(jobId);
        JobKey jobKey = getJobKey(job);

        try {
            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }
        } catch (SchedulerException exception) {
            throw new BizException(50000, "删除定时任务调度失败", exception);
        }

        int deleted = scheduledJobMapper.deleteById(jobId);

        if (deleted != 1) {
            try {
                scheduleJob(job);
            } catch (RuntimeException exception) {
                log.error("恢复已删除定时任务失败，jobId=" + jobId, exception);
            }
            throw new BizException(50000, "删除定时任务失败");
        }
    }

    @Override
    public void reloadScheduledJobs() {
        List<ScheduledJob> jobs = scheduledJobMapper.selectList(null);

        for (ScheduledJob job : jobs) {
            try {
                scheduleJob(job);
            } catch (RuntimeException exception) {
                log.error(
                        "启动恢复定时任务失败，jobId=" + job.getId()
                                + "，taskKey=" + job.getTaskKey(),
                        exception
                );
            }
        }
    }

    private LambdaQueryWrapper<ScheduledJob> buildListWrapper(
            Integer status,
            String keywords
    ) {
        return new LambdaQueryWrapper<ScheduledJob>()
                .eq(status != null, ScheduledJob::getStatus, status)
                .and(StringUtils.hasText(keywords), wrapper -> wrapper
                        .like(ScheduledJob::getJobName, keywords)
                        .or()
                        .like(ScheduledJob::getTaskKey, keywords)
                        .or()
                        .like(ScheduledJob::getJobGroup, keywords));
    }

    private void validateSaveRequest(ScheduledJobSaveRequest request) {
        if (request == null) {
            throw new BizException(40000, "定时任务请求不能为空");
        }

        String taskKey = normalizeText(request.getTaskKey());
        taskRegistry.requireHandler(taskKey);
        request.setTaskKey(taskKey);
        request.setJobName(requireText(request.getJobName(), "任务名称不能为空"));
        request.setJobGroup(
                StringUtils.hasText(request.getJobGroup())
                        ? request.getJobGroup().trim()
                        : DEFAULT_GROUP
        );
        request.setCronExpression(
                requireText(request.getCronExpression(), "Cron 表达式不能为空")
        );

        if (!org.quartz.CronExpression.isValidExpression(
                request.getCronExpression()
        )) {
            throw new BizException(40000, "Cron 表达式无效");
        }

        validateStatus(request.getStatus(), false);

        if (!Integer.valueOf(0).equals(request.getConcurrent())
                && !Integer.valueOf(1).equals(request.getConcurrent())) {
            throw new BizException(40000, "并发设置只能是 0 或 1");
        }

        if (request.getMisfirePolicy() < 0
                || request.getMisfirePolicy() > 3) {
            throw new BizException(40000, "错失策略必须在 0 到 3 之间");
        }

        request.setDescription(normalizeText(request.getDescription()));
    }

    private void applyRequest(
            ScheduledJob job,
            ScheduledJobSaveRequest request
    ) {
        job.setTaskKey(request.getTaskKey());
        job.setJobName(request.getJobName());
        job.setJobGroup(request.getJobGroup());
        job.setCronExpression(request.getCronExpression());
        job.setStatus(request.getStatus());
        job.setConcurrent(request.getConcurrent());
        job.setMisfirePolicy(request.getMisfirePolicy());
        job.setDescription(request.getDescription());
    }

    private void scheduleJob(ScheduledJob job) {
        try {
            JobKey jobKey = getJobKey(job);

            if (scheduler.checkExists(jobKey)) {
                scheduler.deleteJob(jobKey);
            }

            Class<? extends Job> jobClass =
                    Integer.valueOf(CONCURRENT_YES).equals(
                            job.getConcurrent()
                    )
                            ? ConcurrentScheduledTaskQuartzJob.class
                            : DisallowConcurrentScheduledTaskQuartzJob.class;

            var jobDetail = JobBuilder.newJob(jobClass)
                    .withIdentity(jobKey)
                    .usingJobData("jobId", job.getId())
                    .build();

            CronScheduleBuilder scheduleBuilder =
                    CronScheduleBuilder.cronSchedule(
                            job.getCronExpression()
                    );
            applyMisfirePolicy(scheduleBuilder, job.getMisfirePolicy());

            CronTrigger trigger = TriggerBuilder.newTrigger()
                    .withIdentity(getTriggerKey(job))
                    .forJob(jobKey)
                    .withSchedule(scheduleBuilder)
                    .build();

            scheduler.scheduleJob(jobDetail, trigger);

            if (Integer.valueOf(STATUS_PAUSED).equals(job.getStatus())) {
                scheduler.pauseJob(jobKey);
            } else {
                scheduler.resumeJob(jobKey);
            }
        } catch (Exception exception) {
            throw new BizException(
                    50000,
                    "定时任务调度配置失败",
                    exception
            );
        }
    }

    private void changeQuartzStatus(ScheduledJob job) {
        try {
            JobKey jobKey = getJobKey(job);

            if (!scheduler.checkExists(jobKey)) {
                scheduleJob(job);
                return;
            }

            if (Integer.valueOf(STATUS_RUNNING).equals(job.getStatus())) {
                scheduler.resumeJob(jobKey);
            } else {
                scheduler.pauseJob(jobKey);
            }
        } catch (SchedulerException exception) {
            throw new BizException(50000, "修改定时任务状态失败", exception);
        }
    }

    private void applyMisfirePolicy(
            CronScheduleBuilder scheduleBuilder,
            Integer misfirePolicy
    ) {
        switch (misfirePolicy) {
            case 1 -> scheduleBuilder
                    .withMisfireHandlingInstructionIgnoreMisfires();
            case 2 -> scheduleBuilder
                    .withMisfireHandlingInstructionFireAndProceed();
            case 3 -> scheduleBuilder
                    .withMisfireHandlingInstructionDoNothing();
            default -> {
                // Quartz 默认策略
            }
        }
    }

    private ScheduledJob requireJob(Long jobId) {
        if (jobId == null || jobId < 1) {
            throw new BizException(40000, "任务 ID 不正确");
        }

        ScheduledJob job = scheduledJobMapper.selectById(jobId);

        if (job == null) {
            throw new BizException(40400, "定时任务不存在");
        }

        return job;
    }

    private void validateStatus(Integer status, boolean nullable) {
        if (status == null && nullable) {
            return;
        }

        if (!Integer.valueOf(0).equals(status)
                && !Integer.valueOf(1).equals(status)) {
            throw new BizException(40000, "任务状态只能是 0 或 1");
        }
    }

    private String requireText(String value, String message) {
        if (!StringUtils.hasText(value)) {
            throw new BizException(40000, message);
        }
        return value.trim();
    }

    private String normalizeText(String value) {
        return value == null ? "" : value.trim();
    }

    private JobKey getJobKey(ScheduledJob job) {
        return JobKey.jobKey(
                TASK_CLASS_NAME + job.getId(),
                job.getJobGroup()
        );
    }

    private TriggerKey getTriggerKey(ScheduledJob job) {
        return TriggerKey.triggerKey(
                TASK_CLASS_NAME + job.getId(),
                job.getJobGroup()
        );
    }

    private ScheduledJobDTO toDTO(ScheduledJob job) {
        ScheduledJobDTO dto = new ScheduledJobDTO();
        dto.setId(job.getId());
        dto.setTaskKey(job.getTaskKey());
        dto.setJobName(job.getJobName());
        dto.setJobGroup(job.getJobGroup());
        dto.setCronExpression(job.getCronExpression());
        dto.setStatus(job.getStatus());
        dto.setConcurrent(job.getConcurrent());
        dto.setMisfirePolicy(job.getMisfirePolicy());
        dto.setDescription(job.getDescription());
        dto.setCreatedAt(job.getCreatedAt());
        dto.setUpdatedAt(job.getUpdatedAt());
        return dto;
    }
}