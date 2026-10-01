package com.example.bloglite.service.impl;

import com.example.bloglite.config.JwtProperties;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

/**
 * JWT 业务实现
 */
@Service
public class JwtServiceImpl implements JwtService {

    /**
     * HS256 密钥至少需要 256 bit，即 32 字节
     */
    private static final int MIN_SECRET_BYTES = 32;

    private final JwtProperties jwtProperties;
    private final SecretKey secretKey;

    public JwtServiceImpl(JwtProperties jwtProperties) {
        this.jwtProperties = jwtProperties;

        validateProperties();

        this.secretKey = Keys.hmacShaKeyFor(
                jwtProperties.getSecret()
                        .getBytes(StandardCharsets.UTF_8)
        );
    }

    @Override
    public String createToken(Long userId) {
        if (userId == null || userId < 1) {
            throw new BizException(40000, "用户 ID 不正确");
        }

        Instant issuedAt = Instant.now();

        Instant expiresAt = issuedAt.plus(
                jwtProperties.getExpirationMinutes(),
                ChronoUnit.MINUTES
        );

        try {
            return Jwts.builder()
                    .issuer(jwtProperties.getIssuer())
                    .subject(String.valueOf(userId))
                    .issuedAt(Date.from(issuedAt))
                    .expiration(Date.from(expiresAt))
                    .signWith(secretKey)
                    .compact();
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BizException(
                    50000,
                    "登录令牌生成失败",
                    exception
            );
        }
    }

    @Override
    public Long parseUserId(String token) {
        if (token == null || token.isBlank()) {
            throw new BizException(40100, "登录令牌不能为空");
        }

        try {
            Claims claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .requireIssuer(jwtProperties.getIssuer())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();

            String subject = claims.getSubject();

            if (subject == null || subject.isBlank()) {
                throw new BizException(
                        40100,
                        "登录令牌缺少用户信息"
                );
            }

            return Long.valueOf(subject);
        } catch (ExpiredJwtException exception) {
            throw new BizException(
                    40100,
                    "登录令牌已过期",
                    exception
            );
        } catch (JwtException | IllegalArgumentException exception) {
            throw new BizException(
                    40100,
                    "登录令牌无效",
                    exception
            );
        }
    }

    @Override
    public long getExpirationSeconds() {
        return jwtProperties.getExpirationMinutes() * 60;
    }

    /**
     * 校验配置，避免应用使用了不安全或无效的 JWT 配置
     */
    private void validateProperties() {
        if (jwtProperties.getIssuer() == null
                || jwtProperties.getIssuer().isBlank()) {
            throw new IllegalStateException("JWT issuer 不能为空");
        }

        if (jwtProperties.getSecret() == null
                || jwtProperties.getSecret().isBlank()) {
            throw new IllegalStateException("JWT secret 不能为空");
        }

        int secretBytes = jwtProperties.getSecret()
                .getBytes(StandardCharsets.UTF_8)
                .length;

        if (secretBytes < MIN_SECRET_BYTES) {
            throw new IllegalStateException(
                    "JWT secret 至少需要 32 字节"
            );
        }

        if (jwtProperties.getExpirationMinutes() <= 0) {
            throw new IllegalStateException(
                    "JWT expiration-minutes 必须大于 0"
            );
        }
    }
}
