package com.example.bloglite.service.impl;

import com.example.bloglite.dto.SiteConfigDTO;
import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.enums.ArticleCommentStatus;
import com.example.bloglite.event.CommentNotificationEvent;
import com.example.bloglite.service.CommentNotificationService;
import com.example.bloglite.service.SiteService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 评论邮件通知业务服务实现
 */
@Service
public class CommentNotificationServiceImpl
        implements CommentNotificationService {

    private static final String TYPE_TOP_LEVEL_CREATED =
            "TOP_LEVEL_COMMENT_CREATED";
    private static final String TYPE_REVIEW_REQUIRED =
            "COMMENT_REVIEW_REQUIRED";
    private static final String TYPE_REPLY_CREATED =
            "COMMENT_REPLY_CREATED";
    private static final String TYPE_COMMENT_APPROVED =
            "COMMENT_APPROVED";

    private final SiteService siteService;
    private final ApplicationEventPublisher eventPublisher;

    public CommentNotificationServiceImpl(
            SiteService siteService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.siteService = siteService;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public void notifyTopLevelCommentCreated(
            ArticleComment comment,
            Article article
    ) {
        if (!emailNotificationEnabled()
                || comment == null
                || article == null) {
            return;
        }

        publishIfDifferentUser(
                TYPE_TOP_LEVEL_CREATED,
                comment,
                article.getAuthorId(),
                comment.getUserId()
        );
    }

    @Override
    public void notifyReplyCreated(
            ArticleComment reply,
            Article article
    ) {
        if (!emailNotificationEnabled()
                || reply == null
                || article == null) {
            return;
        }

        if (Integer.valueOf(
                ArticleCommentStatus.PENDING.getCode()
        ).equals(reply.getStatus())) {
            publishIfDifferentUser(
                    TYPE_REVIEW_REQUIRED,
                    reply,
                    article.getAuthorId(),
                    reply.getUserId()
            );
            return;
        }

        publishIfDifferentUser(
                TYPE_REPLY_CREATED,
                reply,
                reply.getReplyToUserId(),
                reply.getUserId()
        );
    }

    @Override
    public void notifyCommentApproved(
            ArticleComment comment,
            Article article
    ) {
        if (!emailNotificationEnabled()
                || comment == null
                || article == null
                || !Integer.valueOf(
                ArticleCommentStatus.APPROVED.getCode()
        ).equals(comment.getStatus())) {
            return;
        }

        if (comment.getParentId() != null
                && comment.getReplyToUserId() != null) {
            publishIfDifferentUser(
                    TYPE_REPLY_CREATED,
                    comment,
                    comment.getReplyToUserId(),
                    comment.getUserId()
            );
            return;
        }

        publishEvent(TYPE_COMMENT_APPROVED, comment, comment.getUserId());
    }

    private boolean emailNotificationEnabled() {
        SiteConfigDTO config = siteService.getConfig();
        return Integer.valueOf(1).equals(
                config.getEmailNotificationEnabled()
        );
    }

    private void publishIfDifferentUser(
            String notificationType,
            ArticleComment comment,
            Long recipientUserId,
            Long actorUserId
    ) {
        if (Objects.equals(recipientUserId, actorUserId)) {
            return;
        }
        publishEvent(notificationType, comment, recipientUserId);
    }

    private void publishEvent(
            String notificationType,
            ArticleComment comment,
            Long recipientUserId
    ) {
        if (recipientUserId == null || comment == null
                || comment.getId() == null) {
            return;
        }

        eventPublisher.publishEvent(
                new CommentNotificationEvent(
                        notificationType,
                        comment.getId(),
                        recipientUserId
                )
        );
    }
}