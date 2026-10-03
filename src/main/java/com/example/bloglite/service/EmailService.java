package com.example.bloglite.service;

import java.util.Map;

/**
 * HTML 邮件发送服务
 */
public interface EmailService {

    /**
     * 同步发送 HTML 邮件
     */
    void sendHtml(
            String to,
            String subject,
            String templateName,
            Map<String, Object> variables
    );
}