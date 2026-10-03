package com.example.bloglite.event;

/**
 * 评论邮件通知事件
 */
public record CommentNotificationEvent(
        String notificationType,
        Long commentId,
        Long recipientUserId
) {
}