package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台用户列表与详情
 */
@Data
public class AdminUserDTO {

    private Long id;

    private String username;

    private String nickname;

    private String email;

    private String avatarUrl;

    private Integer status;

    private LocalDateTime lastLoginAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<AdminUserRoleDTO> roles;
}
