package com.example.bloglite.service;

import com.example.bloglite.dto.RoleMenuAssignRequest;
import com.example.bloglite.dto.RolePermissionDTO;
import com.example.bloglite.dto.RoleResourceAssignRequest;

/**
 * 角色权限分配业务接口
 */
public interface RolePermissionService {

    /**
     * 查询角色已有菜单和接口资源
     */
    RolePermissionDTO getRolePermissions(Long roleId);

    /**
     * 事务替换角色菜单
     */
    RolePermissionDTO assignRoleMenus(
            Long roleId,
            RoleMenuAssignRequest request
    );

    /**
     * 事务替换角色接口资源
     */
    RolePermissionDTO assignRoleResources(
            Long roleId,
            RoleResourceAssignRequest request
    );
}
