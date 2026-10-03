package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminMenuDTO;
import com.example.bloglite.dto.AdminMenuSaveRequest;
import com.example.bloglite.service.AdminMenuService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台菜单管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/menus")
@PreAuthorize("hasRole('ADMIN')")
public class AdminMenuController {

    private final AdminMenuService adminMenuService;

    public AdminMenuController(AdminMenuService adminMenuService) {
        this.adminMenuService = adminMenuService;
    }

    /**
     * 查询完整菜单树
     */
    @GetMapping
    public ApiResponse<List<AdminMenuDTO>> listMenuTree() {
        return ApiResponse.success(
                adminMenuService.listMenuTree()
        );
    }

    /**
     * 查询菜单详情
     */
    @GetMapping("/{menuId}")
    public ApiResponse<AdminMenuDTO> getMenuDetail(
            @PathVariable Long menuId
    ) {
        return ApiResponse.success(
                adminMenuService.getMenuDetail(menuId)
        );
    }

    /**
     * 新增菜单
     */
    @PostMapping
    public ApiResponse<AdminMenuDTO> createMenu(
            @Valid @RequestBody AdminMenuSaveRequest request
    ) {
        return ApiResponse.success(
                adminMenuService.createMenu(request)
        );
    }

    /**
     * 编辑菜单
     */
    @PutMapping("/{menuId}")
    public ApiResponse<AdminMenuDTO> updateMenu(
            @PathVariable Long menuId,
            @Valid @RequestBody AdminMenuSaveRequest request
    ) {
        return ApiResponse.success(
                adminMenuService.updateMenu(menuId, request)
        );
    }

    /**
     * 删除未被使用的菜单
     */
    @DeleteMapping("/{menuId}")
    public ApiResponse<Void> deleteMenu(
            @PathVariable Long menuId
    ) {
        adminMenuService.deleteMenu(menuId);
        return ApiResponse.success(null);
    }
}
