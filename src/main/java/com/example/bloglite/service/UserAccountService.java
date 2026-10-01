package com.example.bloglite.service;

import com.example.bloglite.dto.*;

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
     * 登录并返回 JWT 和用户信息
     */
    LoginDTO login(LoginRequest request);

    /**
     * 根据用户 ID 查询脱敏用户信息
     */
    UserDTO getUserById(Long userId);

    /**
     * 修改当前用户资料
     */
    UserDTO updateCurrentUser(
            Long userId,
            UpdateProfileRequest request
    );
}