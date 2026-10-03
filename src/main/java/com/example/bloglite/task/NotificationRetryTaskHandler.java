package com.example.bloglite.task;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.config.TaskProperties;
import com.example.bloglite.entity.NotificationLog;
import com.example.bloglite.mapper.NotificationLogMapper;
import com.example.bloglite.service.EmailService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 失败邮件通知重试任务
 */
@Component
public class NotificationRetryTaskHandler implements ScheduledTaskHandler {

    private static final int STATUS_FAILED = 2;
    private static final int STATUS_SUCCESS = 1;

    private final NotificationLogMapper notificationLogMapper;
    private final EmailService emailService;
    private final TaskProperties taskProperties;

    public NotificationRetryTaskHandler(
            NotificationLogMapper notificationLogMapper,
            EmailService emailService,
            TaskProperties taskProperties
    ) {
        this.notificationLogMapper = notificationLogMapper;
        this.emailService = emailService;
        this.taskProperties = taskProperties;
    }

    @Override
    public String getTaskKey() {
        return "notificationRetry";
    }

    @Override
    public String getTaskName() {
        return "邮件通知重试";
    }

    @Override
    public TaskExecutionResult execute() {
        List<NotificationLog> failedLogs = notificationLogMapper.selectList(
                Wrappers.<NotificationLog>lambdaQuery()
                        .eq(NotificationLog::getStatus, STATUS_FAILED)
                        .lt(
                                NotificationLog::getRetryCount,
                                taskProperties.getNotificationRetryMaxCount()
                        )
                        .orderByAsc(NotificationLog::getId)
                        .last("LIMIT "
                                + taskProperties.getNotificationRetryBatchSize())
        );

        int successCount = 0;
        int failedCount = 0;

        for (NotificationLog notificationLog : failedLogs) {
            try {
                if (!StringUtils.hasText(
                        notificationLog.getRecipientEmail()
                )) {
                    throw new IllegalStateException("接收人邮箱为空");
                }

                Map<String, Object> variables = new HashMap<>();
                variables.put(
                        "notificationType",
                        notificationLog.getNotificationType()
                );
                variables.put("businessId", notificationLog.getBusinessId());
                emailService.sendHtml(
                        notificationLog.getRecipientEmail(),
                        "blog-lite：评论通知重试",
                        "notification-retry.html",
                        variables
                );
                notificationLog.setStatus(STATUS_SUCCESS);
                notificationLog.setSentAt(LocalDateTime.now());
                notificationLog.setLastError(null);
                successCount++;
            } catch (RuntimeException exception) {
                notificationLog.setRetryCount(
                        notificationLog.getRetryCount() + 1
                );
                notificationLog.setLastError(
                        truncate(exception.getMessage(), 1000)
                );
                failedCount++;
            }

            notificationLog.setUpdatedAt(LocalDateTime.now());
            notificationLogMapper.updateById(notificationLog);
        }

        return TaskExecutionResult.success(
                "邮件通知重试完成，成功 " + successCount
                        + " 条，失败 " + failedCount + " 条"
        );
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength
                ? value
                : value.substring(0, maxLength);
    }
}