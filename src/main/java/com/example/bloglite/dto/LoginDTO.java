package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 登录成功返回结果
 */
@Data
@AllArgsConstructor
public class LoginDTO {

    /**
     * JWT 字符串
     */
    private String token;

    /**
     * 令牌类型，固定为 Bearer
     */
    private String tokenType;

    /**
     * 令牌有效期，单位：秒
     */
    private long expiresIn;

    /**
     * 脱敏后的用户信息
     */
    private UserDTO user;
}