package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评论审核列表返回数据
 */
@Data
public class ArticleCommentReviewDTO {

    /**
     * 评论 ID
     */
    private Long id;

    /**
     * 文章 ID
     */
    private Long articleId;

    /**
     * 所属顶级评论 ID
     */
    private Long parentId;

    /**
     * 评论用户 ID
     */
    private Long userId;

    /**
     * 评论用户昵称
     */
    private String nickname;

    /**
     * 评论用户头像
     */
    private String avatarUrl;

    /**
     * 实际被回复用户 ID
     */
    private Long replyToUserId;

    /**
     * 实际被回复用户昵称
     */
    private String replyToNickname;

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
     * 评论创建时间
     */
    private LocalDateTime createdAt;
}