package com.example.bloglite.config;

import com.example.bloglite.service.ScheduledJobService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

/**
 * 应用启动后恢复数据库中的定时任务
 */
@Slf4j
@Component
public class ScheduledJobInitializer implements ApplicationRunner {

    private final ScheduledJobService scheduledJobService;

    public ScheduledJobInitializer(
            ScheduledJobService scheduledJobService
    ) {
        this.scheduledJobService = scheduledJobService;
    }

    @Override
    public void run(ApplicationArguments args) {
        try {
            scheduledJobService.reloadScheduledJobs();
        } catch (RuntimeException exception) {
            log.error("定时任务启动恢复失败", exception);
        }
    }
}