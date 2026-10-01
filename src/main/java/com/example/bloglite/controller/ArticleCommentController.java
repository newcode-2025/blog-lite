package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.CreateCommentRequest;
import com.example.bloglite.service.ArticleCommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章评论接口
 */
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleCommentController {

    private final ArticleCommentService articleCommentService;

    public ArticleCommentController(
            ArticleCommentService articleCommentService
    ) {
        this.articleCommentService = articleCommentService;
    }

    /**
     * 在指定文章下发表顶级评论
     */
    @PostMapping("/{articleId}/comments")
    public ApiResponse<ArticleCommentDTO> createTopLevelComment(
            @PathVariable("articleId") Long articleId,
            @Valid @RequestBody CreateCommentRequest request
    ) {
        return ApiResponse.success(
                articleCommentService.createTopLevelComment(
                        articleId,
                        request
                )
        );
    }
}