package com.example.bloglite.quartz;

import com.example.bloglite.service.impl.ScheduledTaskExecutionService;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Component;

/**
 * 允许并发的 Quartz 任务
 */
@Component
public class ConcurrentScheduledTaskQuartzJob implements Job {

    private final ScheduledTaskExecutionService executionService;

    public ConcurrentScheduledTaskQuartzJob(
            ScheduledTaskExecutionService executionService
    ) {
        this.executionService = executionService;
    }

    @Override
    public void execute(JobExecutionContext context)
            throws JobExecutionException {
        Long jobId = context.getMergedJobDataMap().getLong("jobId");
        executionService.execute(jobId, "SCHEDULE");
    }
}