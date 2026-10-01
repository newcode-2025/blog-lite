package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.ArticleReplyDTO;
import com.example.bloglite.dto.CreateReplyRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.ArticleCommentService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 评论回复接口
 */
@RestController
@RequestMapping("/api/v1/comments")
public class CommentController {

    private final ArticleCommentService articleCommentService;

    public CommentController(
            ArticleCommentService articleCommentService
    ) {
        this.articleCommentService = articleCommentService;
    }

    /**
     * 回复顶级评论或某条回复
     */
    @PostMapping("/{targetCommentId}/replies")
    public ApiResponse<ArticleCommentDTO> createReply(
            @PathVariable("targetCommentId") Long targetCommentId,
            @Valid @RequestBody CreateReplyRequest request
    ) {
        return ApiResponse.success(
                articleCommentService.createReply(
                        targetCommentId,
                        request
                )
        );
    }

    /**
     * 分页查询某条顶级评论下的公开回复
     */
    @GetMapping("/{commentId}/replies")
    public ApiResponse<PageResultDTO<ArticleReplyDTO>>
    listPublicReplies(
            @PathVariable("commentId") Long commentId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                articleCommentService.listPublicReplies(
                        commentId,
                        current,
                        size
                )
        );
    }

    /**
     * 删除当前用户自己的评论
     */
    @DeleteMapping("/{commentId}")
    public ApiResponse<Void> deleteOwnComment(
            @PathVariable("commentId") Long commentId
    ) {
        articleCommentService.deleteOwnComment(commentId);

        return ApiResponse.success(null);
    }
}