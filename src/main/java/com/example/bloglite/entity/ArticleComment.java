package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 文章评论实体
 */
@Data
@TableName("article_comment")
public class ArticleComment {

    @TableId(value = "id", type = IdType.AUTO)
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
     * 顶级评论为 null；回复保存顶级评论 ID
     */
    private Long parentId;

    /**
     * 被回复用户 ID
     * 顶级评论为 null；回复时保存实际被回复的用户 ID
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
     * 是否软删除
     * 0：正常，1：已删除
     */
    private Integer isDeleted;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}