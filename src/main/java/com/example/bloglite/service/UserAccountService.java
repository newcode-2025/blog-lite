package com.example.bloglite.service;

import com.example.bloglite.dto.LoginRequest;
import com.example.bloglite.dto.RegisterRequest;
import com.example.bloglite.dto.UserDTO;

/**
 * 用户账号业务接口
 */
public interface UserAccountService {

    /**
     * 注册新用户
     *
     * @param request 注册请求
     * @return 脱敏后的用户信息
     */
    UserDTO register(RegisterRequest request);

    /**
     * 校验登录用户名和密码
     *
     * @param request 登录请求
     * @return 脱敏后的用户信息
     */
    UserDTO login(LoginRequest request);
}