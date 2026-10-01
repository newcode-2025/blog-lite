package com.example.bloglite.controller;

import com.example.bloglite.comment.ApiResponse;
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
     *
     * @Valid 会先执行 RegisterRequest 上的参数校验
     * 校验失败或业务异常时，由 GlobalExceptionHandler 统一返回
     */
    @PostMapping("/register")
    public ApiResponse<UserDTO> register(
            @Valid @RequestBody RegisterRequest request
    ) {
        return ApiResponse.success(
                userAccountService.register(request)
        );
    }
}