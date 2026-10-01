package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公开评论列表返回数据
 */
@Data
public class ArticleCommentListDTO {

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
     * 评论用户昵称
     */
    private String nickname;

    /**
     * 评论用户头像地址
     */
    private String avatarUrl;

    /**
     * 评论内容
     */
    private String content;

    /**
     * 评论创建时间
     */
    private LocalDateTime createdAt;
}