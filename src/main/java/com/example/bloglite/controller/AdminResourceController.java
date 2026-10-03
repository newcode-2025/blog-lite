package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminResourceDTO;
import com.example.bloglite.dto.AdminResourceSaveRequest;
import com.example.bloglite.service.AdminResourceService;
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
 * 后台接口资源管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/resources")
@PreAuthorize("hasRole('ADMIN')")
public class AdminResourceController {

    private final AdminResourceService adminResourceService;

    public AdminResourceController(
            AdminResourceService adminResourceService
    ) {
        this.adminResourceService = adminResourceService;
    }

    /**
     * 查询完整接口资源树
     */
    @GetMapping
    public ApiResponse<List<AdminResourceDTO>> listResourceTree() {
        return ApiResponse.success(
                adminResourceService.listResourceTree()
        );
    }

    /**
     * 查询接口资源详情
     */
    @GetMapping("/{resourceId}")
    public ApiResponse<AdminResourceDTO> getResourceDetail(
            @PathVariable Long resourceId
    ) {
        return ApiResponse.success(
                adminResourceService.getResourceDetail(resourceId)
        );
    }

    /**
     * 新增接口资源
     */
    @PostMapping
    public ApiResponse<AdminResourceDTO> createResource(
            @Valid @RequestBody AdminResourceSaveRequest request
    ) {
        return ApiResponse.success(
                adminResourceService.createResource(request)
        );
    }

    /**
     * 编辑接口资源
     */
    @PutMapping("/{resourceId}")
    public ApiResponse<AdminResourceDTO> updateResource(
            @PathVariable Long resourceId,
            @Valid @RequestBody AdminResourceSaveRequest request
    ) {
        return ApiResponse.success(
                adminResourceService.updateResource(resourceId, request)
        );
    }

    /**
     * 删除未被使用的接口资源
     */
    @DeleteMapping("/{resourceId}")
    public ApiResponse<Void> deleteResource(
            @PathVariable Long resourceId
    ) {
        adminResourceService.deleteResource(resourceId);
        return ApiResponse.success(null);
    }
}
