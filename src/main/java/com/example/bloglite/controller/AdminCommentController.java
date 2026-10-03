package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminCommentDTO;
import com.example.bloglite.dto.BatchReviewCommentsRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.AdminCommentService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台评论管理接口
 */
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RequestMapping("/api/v1/admin/comments")
public class AdminCommentController {

    private final AdminCommentService adminCommentService;

    public AdminCommentController(
            AdminCommentService adminCommentService
    ) {
        this.adminCommentService = adminCommentService;
    }

    /**
     * 分页查询当前用户文章下的评论
     */
    @GetMapping
    public ApiResponse<PageResultDTO<AdminCommentDTO>>
    listComments(
            @RequestParam(required = false) Long articleId,
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                adminCommentService.listComments(
                        articleId,
                        status,
                        keywords,
                        current,
                        size
                )
        );
    }

    /**
     * 批量审核评论
     */
    @PutMapping("/status")
    public ApiResponse<Void> batchReviewComments(
            @Valid @RequestBody BatchReviewCommentsRequest request
    ) {
        adminCommentService.batchReviewComments(request);
        return ApiResponse.success(null);
    }
}
