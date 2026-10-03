package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台角色列表与详情
 */
@Data
public class AdminRoleDTO {

    private Long id;

    private String roleCode;

    private String roleName;

    private String description;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
