package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminArticleDetailDTO;
import com.example.bloglite.dto.AdminArticleFlagsUpdateRequest;
import com.example.bloglite.dto.AdminArticleListItemDTO;
import com.example.bloglite.dto.AdminArticleStatusUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.AdminArticleLifecycleService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台文章生命周期接口
 */
@RestController
@PreAuthorize("hasAnyRole('ADMIN','USER')")
@RequestMapping("/api/v1/admin/articles")
public class AdminArticleLifecycleController {

    private final AdminArticleLifecycleService lifecycleService;

    public AdminArticleLifecycleController(
            AdminArticleLifecycleService lifecycleService
    ) {
        this.lifecycleService = lifecycleService;
    }

    /**
     * 修改文章状态
     */
    @PutMapping("/{articleId}/status")
    public ApiResponse<AdminArticleDetailDTO> updateStatus(
            @PathVariable Long articleId,
            @Valid @RequestBody AdminArticleStatusUpdateRequest request
    ) {
        return ApiResponse.success(
                lifecycleService.updateStatus(articleId, request)
        );
    }

    /**
     * 修改文章置顶和推荐
     */
    @PutMapping("/{articleId}/flags")
    public ApiResponse<AdminArticleDetailDTO> updateFlags(
            @PathVariable Long articleId,
            @Valid @RequestBody AdminArticleFlagsUpdateRequest request
    ) {
        return ApiResponse.success(
                lifecycleService.updateFlags(articleId, request)
        );
    }

    /**
     * 软删除文章
     */
    @DeleteMapping("/{articleId}")
    public ApiResponse<Void> softDeleteArticle(
            @PathVariable Long articleId
    ) {
        lifecycleService.softDeleteArticle(articleId);
        return ApiResponse.success(null);
    }

    /**
     * 恢复软删除文章
     */
    @PutMapping("/{articleId}/restore")
    public ApiResponse<AdminArticleDetailDTO> restoreArticle(
            @PathVariable Long articleId
    ) {
        return ApiResponse.success(
                lifecycleService.restoreArticle(articleId)
        );
    }

    /**
     * 分页查询当前用户回收站
     */
    @GetMapping("/recycle-bin")
    public ApiResponse<PageResultDTO<AdminArticleListItemDTO>>
    listRecycleBin(
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                lifecycleService.listRecycleBin(
                        keywords,
                        current,
                        size
                )
        );
    }
}
