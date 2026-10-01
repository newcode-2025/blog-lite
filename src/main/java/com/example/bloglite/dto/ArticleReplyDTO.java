package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 公开评论回复返回数据
 */
@Data
public class ArticleReplyDTO {

    /**
     * 回复 ID
     */
    private Long id;

    /**
     * 所属文章 ID
     */
    private Long articleId;

    /**
     * 所属顶级评论 ID
     */
    private Long parentId;

    /**
     * 回复用户 ID
     */
    private Long userId;

    /**
     * 回复用户昵称
     */
    private String nickname;

    /**
     * 回复用户头像
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
     * 回复内容
     */
    private String content;

    /**
     * 回复时间
     */
    private LocalDateTime createdAt;
}