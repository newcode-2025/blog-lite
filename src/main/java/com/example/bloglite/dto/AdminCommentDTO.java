package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台评论管理数据
 */
@Data
public class AdminCommentDTO {

    private Long id;

    private Long articleId;

    private String articleTitle;

    private Long parentId;

    private Long userId;

    private String nickname;

    private String avatarUrl;

    private Long replyToUserId;

    private String replyToNickname;

    private String content;

    /**
     * 0 待审核，1 已通过，2 已拒绝
     */
    private Integer status;

    private LocalDateTime createdAt;
}