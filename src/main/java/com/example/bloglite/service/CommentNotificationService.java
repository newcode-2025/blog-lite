package com.example.bloglite.service;

import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.ArticleComment;

/**
 * 评论邮件通知业务服务
 */
public interface CommentNotificationService {

    /**
     * 顶级评论创建后发送通知
     */
    void notifyTopLevelCommentCreated(
            ArticleComment comment,
            Article article
    );

    /**
     * 回复创建后发送通知
     */
    void notifyReplyCreated(
            ArticleComment reply,
            Article article
    );

    /**
     * 评论审核通过后发送通知
     */
    void notifyCommentApproved(
            ArticleComment comment,
            Article article
    );
}