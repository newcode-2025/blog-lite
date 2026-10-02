package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminCategoryCreateRequest;
import com.example.bloglite.dto.AdminCategoryDTO;
import com.example.bloglite.dto.AdminCategoryUpdateRequest;
import com.example.bloglite.service.AdminCategoryService;
import jakarta.validation.Valid;
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
 * 后台分类管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/categories")
public class AdminCategoryController {

    private final AdminCategoryService adminCategoryService;

    public AdminCategoryController(
            AdminCategoryService adminCategoryService
    ) {
        this.adminCategoryService = adminCategoryService;
    }

    /**
     * 查询全部分类
     */
    @GetMapping
    public ApiResponse<List<AdminCategoryDTO>> listCategories() {
        return ApiResponse.success(
                adminCategoryService.listCategories()
        );
    }

    /**
     * 新建分类
     */
    @PostMapping
    public ApiResponse<AdminCategoryDTO> createCategory(
            @Valid @RequestBody AdminCategoryCreateRequest request
    ) {
        return ApiResponse.success(
                adminCategoryService.createCategory(request)
        );
    }

    /**
     * 编辑分类
     */
    @PutMapping("/{categoryId}")
    public ApiResponse<AdminCategoryDTO> updateCategory(
            @PathVariable Long categoryId,
            @Valid @RequestBody AdminCategoryUpdateRequest request
    ) {
        return ApiResponse.success(
                adminCategoryService.updateCategory(
                        categoryId,
                        request
                )
        );
    }

    /**
     * 删除未被文章使用的分类
     */
    @DeleteMapping("/{categoryId}")
    public ApiResponse<Void> deleteCategory(
            @PathVariable Long categoryId
    ) {
        adminCategoryService.deleteCategory(categoryId);
        return ApiResponse.success(null);
    }
}