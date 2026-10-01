package com.example.bloglite.controller;

import com.example.bloglite.comment.ApiResponse;
import com.example.bloglite.dto.LoginDTO;
import com.example.bloglite.dto.LoginRequest;
import com.example.bloglite.dto.RegisterRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.service.UserAccountService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证相关接口
 */
@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final UserAccountService userAccountService;

    public AuthController(UserAccountService userAccountService) {
        this.userAccountService = userAccountService;
    }

    /**
     * 用户注册
     */
    @PostMapping("/register")
    public ApiResponse<UserDTO> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ApiResponse.success(
                userAccountService.register(request)
        );
    }

    /**
     * 用户登录并获取 JWT
     */
    @PostMapping("/login")
    public ApiResponse<LoginDTO> login(
            @Valid @RequestBody LoginRequest request
    ) {
        return ApiResponse.success(
                userAccountService.login(request)
        );
    }
}