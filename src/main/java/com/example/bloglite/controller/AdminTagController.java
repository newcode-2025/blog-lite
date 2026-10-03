package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminTagCreateRequest;
import com.example.bloglite.dto.AdminTagDTO;
import com.example.bloglite.dto.AdminTagUpdateRequest;
import com.example.bloglite.service.AdminTagService;
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
 * 后台标签管理接口
 */
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/tags")
public class AdminTagController {

    private final AdminTagService adminTagService;

    public AdminTagController(AdminTagService adminTagService) {
        this.adminTagService = adminTagService;
    }

    /**
     * 查询全部标签
     */
    @GetMapping
    public ApiResponse<List<AdminTagDTO>> listTags() {
        return ApiResponse.success(adminTagService.listTags());
    }

    /**
     * 新建标签
     */
    @PostMapping
    public ApiResponse<AdminTagDTO> createTag(
            @Valid @RequestBody AdminTagCreateRequest request
    ) {
        return ApiResponse.success(
                adminTagService.createTag(request)
        );
    }

    /**
     * 编辑标签
     */
    @PutMapping("/{tagId}")
    public ApiResponse<AdminTagDTO> updateTag(
            @PathVariable Long tagId,
            @Valid @RequestBody AdminTagUpdateRequest request
    ) {
        return ApiResponse.success(
                adminTagService.updateTag(tagId, request)
        );
    }

    /**
     * 删除未被文章使用的标签
     */
    @DeleteMapping("/{tagId}")
    public ApiResponse<Void> deleteTag(
            @PathVariable Long tagId
    ) {
        adminTagService.deleteTag(tagId);
        return ApiResponse.success(null);
    }
}
