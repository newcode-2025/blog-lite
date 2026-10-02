package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminArticleCreateRequest;
import com.example.bloglite.dto.AdminArticleCreateResultDTO;
import com.example.bloglite.dto.AdminArticleDetailDTO;
import com.example.bloglite.dto.AdminArticleListItemDTO;
import com.example.bloglite.dto.AdminArticleUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.AdminArticleService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台文章管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/articles")
public class AdminArticleController {

    private final AdminArticleService adminArticleService;

    public AdminArticleController(
            AdminArticleService adminArticleService
    ) {
        this.adminArticleService = adminArticleService;
    }

    /**
     * 分页查询当前登录用户的文章
     */
    @GetMapping
    public ApiResponse<PageResultDTO<AdminArticleListItemDTO>>
    listArticles(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                adminArticleService.listArticles(
                        status,
                        keywords,
                        current,
                        size
                )
        );
    }

    /**
     * 新建当前登录用户的文章
     */
    @PostMapping
    public ApiResponse<AdminArticleCreateResultDTO> createArticle(
            @Valid @RequestBody AdminArticleCreateRequest request
    ) {
        return ApiResponse.success(
                adminArticleService.createArticle(request)
        );
    }

    /**
     * 查询当前登录用户的文章编辑详情
     */
    @GetMapping("/{articleId}")
    public ApiResponse<AdminArticleDetailDTO> getArticleDetail(
            @PathVariable Long articleId
    ) {
        return ApiResponse.success(
                adminArticleService.getArticleDetail(articleId)
        );
    }

    /**
     * 更新当前登录用户的文章
     */
    @PutMapping("/{articleId}")
    public ApiResponse<AdminArticleDetailDTO> updateArticle(
            @PathVariable Long articleId,
            @Valid @RequestBody AdminArticleUpdateRequest request
    ) {
        return ApiResponse.success(
                adminArticleService.updateArticle(
                        articleId,
                        request
                )
        );
    }
}