package com.example.bloglite.dto;

import lombok.Data;

/**
 * 后台用户角色信息
 */
@Data
public class AdminUserRoleDTO {

    private Long userId;

    private Long roleId;

    private String roleCode;

    private String roleName;

    private Integer status;
}
