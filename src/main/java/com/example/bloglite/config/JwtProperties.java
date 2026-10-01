package com.example.bloglite.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT 配置属性
 * 对应 application.yaml 中的 blog.jwt 配置
 */
@Data
@Component
@ConfigurationProperties(prefix = "blog.jwt")
public class JwtProperties {

    /**
     * JWT 签发者
     */
    private String issuer;

    /**
     * JWT 签名密钥
     */
    private String secret;

    /**
     * 令牌有效期，单位：分钟
     */
    private long expirationMinutes;
}
