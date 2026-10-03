package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.RoleMenuAssignRequest;
import com.example.bloglite.dto.RolePermissionDTO;
import com.example.bloglite.dto.RoleResourceAssignRequest;
import com.example.bloglite.service.RolePermissionService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台角色权限分配接口
 */
@RestController
@RequestMapping("/api/v1/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRolePermissionController {

    private final RolePermissionService rolePermissionService;

    public AdminRolePermissionController(
            RolePermissionService rolePermissionService
    ) {
        this.rolePermissionService = rolePermissionService;
    }

    /**
     * 查询角色已有权限
     */
    @GetMapping("/{roleId}/permissions")
    public ApiResponse<RolePermissionDTO> getRolePermissions(
            @PathVariable Long roleId
    ) {
        return ApiResponse.success(
                rolePermissionService.getRolePermissions(roleId)
        );
    }

    /**
     * 替换角色菜单
     */
    @PutMapping("/{roleId}/menus")
    public ApiResponse<RolePermissionDTO> assignRoleMenus(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleMenuAssignRequest request
    ) {
        return ApiResponse.success(
                rolePermissionService.assignRoleMenus(roleId, request)
        );
    }

    /**
     * 替换角色接口资源
     */
    @PutMapping("/{roleId}/resources")
    public ApiResponse<RolePermissionDTO> assignRoleResources(
            @PathVariable Long roleId,
            @Valid @RequestBody RoleResourceAssignRequest request
    ) {
        return ApiResponse.success(
                rolePermissionService.assignRoleResources(roleId, request)
        );
    }
}
