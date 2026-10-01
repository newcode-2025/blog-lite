package com.example.bloglite.service;

/**
 * JWT 业务接口
 */
public interface JwtService {

    /**
     * 根据用户 ID 创建 JWT
     *
     * @param userId 用户 ID
     * @return JWT 字符串
     */
    String createToken(Long userId);

    /**
     * 校验 JWT，并从中解析用户 ID
     *
     * @param token JWT 字符串
     * @return 用户 ID
     */
    Long parseUserId(String token);

    /**
     * 获取令牌有效期，单位：秒
     */
    long getExpirationSeconds();
}