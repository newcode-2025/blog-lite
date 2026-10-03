package com.example.bloglite.dto;

import lombok.Data;

import java.util.List;

/**
 * 角色菜单和接口资源权限
 */
@Data
public class RolePermissionDTO {

    private Long roleId;

    private String roleCode;

    private String roleName;

    private List<Long> menuIds;

    private List<Long> resourceIds;
}
