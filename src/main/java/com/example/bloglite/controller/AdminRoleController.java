package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminRoleCreateRequest;
import com.example.bloglite.dto.AdminRoleDTO;
import com.example.bloglite.dto.AdminRoleOptionDTO;
import com.example.bloglite.dto.AdminRoleUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.AdminRoleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台角色管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class AdminRoleController {

    private final AdminRoleService adminRoleService;

    public AdminRoleController(AdminRoleService adminRoleService) {
        this.adminRoleService = adminRoleService;
    }

    /**
     * 分页查询角色
     */
    @GetMapping
    public ApiResponse<PageResultDTO<AdminRoleDTO>> listRoles(
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                adminRoleService.listRoles(
                        keywords,
                        status,
                        current,
                        size
                )
        );
    }

    /**
     * 查询可分配角色选项
     */
    @GetMapping("/options")
    public ApiResponse<List<AdminRoleOptionDTO>> listRoleOptions() {
        return ApiResponse.success(
                adminRoleService.listRoleOptions()
        );
    }

    /**
     * 查询角色详情
     */
    @GetMapping("/{roleId}")
    public ApiResponse<AdminRoleDTO> getRoleDetail(
            @PathVariable Long roleId
    ) {
        return ApiResponse.success(
                adminRoleService.getRoleDetail(roleId)
        );
    }

    /**
     * 新增角色
     */
    @PostMapping
    public ApiResponse<AdminRoleDTO> createRole(
            @Valid @RequestBody AdminRoleCreateRequest request
    ) {
        return ApiResponse.success(
                adminRoleService.createRole(request)
        );
    }

    /**
     * 编辑角色
     */
    @PutMapping("/{roleId}")
    public ApiResponse<AdminRoleDTO> updateRole(
            @PathVariable Long roleId,
            @Valid @RequestBody AdminRoleUpdateRequest request
    ) {
        return ApiResponse.success(
                adminRoleService.updateRole(roleId, request)
        );
    }

    /**
     * 删除未被使用的普通角色
     */
    @DeleteMapping("/{roleId}")
    public ApiResponse<Void> deleteRole(
            @PathVariable Long roleId
    ) {
        adminRoleService.deleteRole(roleId);
        return ApiResponse.success(null);
    }
}
