package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章评论返回数据
 */
@Data
public class ArticleCommentDTO {

    /**
     * 评论 ID
     */
    private Long id;

    /**
     * 文章 ID
     */
    private Long articleId;

    /**
     * 评论用户 ID
     */
    private Long userId;

    /**
     * 所属顶级评论 ID
     * 顶级评论为 null
     */
    private Long parentId;

    /**
     * 被回复用户 ID
     * 顶级评论为 null
     */
    private Long replyToUserId;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 审核状态
     * 0：待审核，1：已通过，2：已拒绝
     */
    private Integer status;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}