package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminUserDTO;
import com.example.bloglite.dto.AdminUserRoleUpdateRequest;
import com.example.bloglite.dto.AdminUserStatusUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.AdminUserService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台用户管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/users")
@PreAuthorize("hasRole('ADMIN')")
public class AdminUserController {

    private final AdminUserService adminUserService;

    public AdminUserController(AdminUserService adminUserService) {
        this.adminUserService = adminUserService;
    }

    /**
     * 分页查询用户
     */
    @GetMapping
    public ApiResponse<PageResultDTO<AdminUserDTO>> listUsers(
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                adminUserService.listUsers(
                        keywords,
                        status,
                        current,
                        size
                )
        );
    }

    /**
     * 查询用户详情
     */
    @GetMapping("/{userId}")
    public ApiResponse<AdminUserDTO> getUserDetail(
            @PathVariable Long userId
    ) {
        return ApiResponse.success(
                adminUserService.getUserDetail(userId)
        );
    }

    /**
     * 修改用户启用状态
     */
    @PutMapping("/{userId}/status")
    public ApiResponse<AdminUserDTO> updateUserStatus(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserStatusUpdateRequest request
    ) {
        return ApiResponse.success(
                adminUserService.updateUserStatus(userId, request)
        );
    }

    /**
     * 替换用户角色
     */
    @PutMapping("/{userId}/roles")
    public ApiResponse<AdminUserDTO> updateUserRoles(
            @PathVariable Long userId,
            @Valid @RequestBody AdminUserRoleUpdateRequest request
    ) {
        return ApiResponse.success(
                adminUserService.updateUserRoles(userId, request)
        );
    }
}
