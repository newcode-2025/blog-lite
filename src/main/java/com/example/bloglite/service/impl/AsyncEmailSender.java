package com.example.bloglite.service.impl;

import com.example.bloglite.exception.BizException;
import com.example.bloglite.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * 异步邮件发送通道
 */
@Slf4j
@Service
public class AsyncEmailSender {

    private final EmailService emailService;

    public AsyncEmailSender(EmailService emailService) {
        this.emailService = emailService;
    }

    /**
     * 异步发送邮件，失败只记录日志，不影响业务事务
     */
    @Async("mailExecutor")
    public void sendHtml(
            String to,
            String subject,
            String templateName,
            Map<String, Object> variables
    ) {
        try {
            emailService.sendHtml(to, subject, templateName, variables);
        } catch (BizException exception) {
            log.error(
                    "异步邮件发送失败，recipient={}，原因={}",
                    to,
                    exception.getMessage()
            );
        } catch (RuntimeException exception) {
            log.error("异步邮件发送发生未知异常，recipient=" + to, exception);
        }
    }
}