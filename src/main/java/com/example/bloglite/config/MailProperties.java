package com.example.bloglite.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * 邮件配置属性
 * 对应 application.yaml 中的 blog.mail 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog.mail")
public class MailProperties {

    /**
     * 是否启用邮件功能
     */
    private boolean enabled;

    /**
     * 发件人邮箱
     */
    private String from;

    /**
     * 发件人显示名称
     */
    private String fromName;
}