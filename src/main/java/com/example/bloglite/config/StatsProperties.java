package com.example.bloglite.config;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.validation.annotation.Validated;

/**
 * 访问统计配置属性
 * 对应 application.yaml 中的 blog.stats 配置
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "blog.stats")
public class StatsProperties {

    /**
     * 访客哈希盐值，禁止保存原始 IP 作为统计访客标识
     */
    @NotBlank(message = "访问统计哈希盐值不能为空")
    private String visitorHashSalt;
}