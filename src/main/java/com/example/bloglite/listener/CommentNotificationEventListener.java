package com.example.bloglite.listener;

import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.entity.NotificationLog;
import com.example.bloglite.entity.UserAccount;
import com.example.bloglite.event.CommentNotificationEvent;
import com.example.bloglite.mapper.ArticleCommentMapper;
import com.example.bloglite.mapper.ArticleMapper;
import com.example.bloglite.mapper.NotificationLogMapper;
import com.example.bloglite.mapper.UserAccountMapper;
import com.example.bloglite.service.EmailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * 评论邮件通知事件监听器
 */
@Slf4j
@Component
public class CommentNotificationEventListener {

    private static final String BUSINESS_TYPE = "ARTICLE_COMMENT";
    private static final int STATUS_PENDING = 0;
    private static final int STATUS_SUCCESS = 1;
    private static final int STATUS_FAILED = 2;
    private static final int USER_STATUS_ENABLED = 1;
    private static final int MAX_CONTENT_PREVIEW = 160;
    private static final int MAX_ERROR_LENGTH = 1000;

    private final NotificationLogMapper notificationLogMapper;
    private final ArticleCommentMapper articleCommentMapper;
    private final ArticleMapper articleMapper;
    private final UserAccountMapper userAccountMapper;
    private final EmailService emailService;

    public CommentNotificationEventListener(
            NotificationLogMapper notificationLogMapper,
            ArticleCommentMapper articleCommentMapper,
            ArticleMapper articleMapper,
            UserAccountMapper userAccountMapper,
            EmailService emailService
    ) {
        this.notificationLogMapper = notificationLogMapper;
        this.articleCommentMapper = articleCommentMapper;
        this.articleMapper = articleMapper;
        this.userAccountMapper = userAccountMapper;
        this.emailService = emailService;
    }

    /**
     * 在评论事务提交后异步发送通知
     */
    @Async("mailExecutor")
    @TransactionalEventListener(
            phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true
    )
    public void handleCommentNotification(
            CommentNotificationEvent event
    ) {
        if (event == null || event.commentId() == null
                || event.recipientUserId() == null) {
            return;
        }

        ArticleComment comment = articleCommentMapper.selectById(
                event.commentId()
        );
        Article article = comment == null
                ? null
                : articleMapper.selectById(comment.getArticleId());
        UserAccount recipient = userAccountMapper.selectById(
                event.recipientUserId()
        );

        if (comment == null || article == null
                || recipient == null
                || !Integer.valueOf(USER_STATUS_ENABLED)
                .equals(recipient.getStatus())
                || !StringUtils.hasText(recipient.getEmail())) {
            return;
        }

        NotificationLog notificationLog = createNotificationLog(
                event,
                recipient
        );

        try {
            notificationLogMapper.insert(notificationLog);
        } catch (DuplicateKeyException exception) {
            log.debug(
                    "评论通知已存在，跳过重复发送，type={}，commentId={}，recipient={}",
                    event.notificationType(),
                    event.commentId(),
                    event.recipientUserId()
            );
            return;
        }

        try {
            EmailContent emailContent = buildEmailContent(
                    event,
                    comment,
                    article,
                    recipient
            );
            emailService.sendHtml(
                    recipient.getEmail(),
                    emailContent.subject(),
                    "comment-notification.html",
                    emailContent.variables()
            );
            notificationLog.setStatus(STATUS_SUCCESS);
            notificationLog.setSentAt(LocalDateTime.now());
            notificationLog.setLastError(null);
        } catch (RuntimeException exception) {
            notificationLog.setStatus(STATUS_FAILED);
            notificationLog.setLastError(
                    truncate(exception.getMessage(), MAX_ERROR_LENGTH)
            );
            log.error(
                    "评论邮件发送失败，commentId={}，recipient={}",
                    event.commentId(),
                    event.recipientUserId(),
                    exception
            );
        }

        notificationLogMapper.updateById(notificationLog);
    }

    private NotificationLog createNotificationLog(
            CommentNotificationEvent event,
            UserAccount recipient
    ) {
        LocalDateTime now = LocalDateTime.now();
        NotificationLog notificationLog = new NotificationLog();
        notificationLog.setNotificationType(event.notificationType());
        notificationLog.setBusinessType(BUSINESS_TYPE);
        notificationLog.setBusinessId(event.commentId());
        notificationLog.setRecipientUserId(event.recipientUserId());
        notificationLog.setRecipientEmail(recipient.getEmail());
        notificationLog.setStatus(STATUS_PENDING);
        notificationLog.setRetryCount(0);
        notificationLog.setCreatedAt(now);
        notificationLog.setUpdatedAt(now);
        return notificationLog;
    }

    private EmailContent buildEmailContent(
            CommentNotificationEvent event,
            ArticleComment comment,
            Article article,
            UserAccount recipient
    ) {
        String title;
        String message;
        String subject;

        switch (event.notificationType()) {
            case "TOP_LEVEL_COMMENT_CREATED" -> {
                title = "你的文章收到了新评论";
                message = "有一条新评论已经提交，请及时查看。";
                subject = "blog-lite：文章收到新评论";
            }
            case "COMMENT_REVIEW_REQUIRED" -> {
                title = "有一条评论待审核";
                message = "你的文章下有新的评论或回复等待审核。";
                subject = "blog-lite：评论待审核";
            }
            case "COMMENT_REPLY_CREATED" -> {
                title = "你的评论收到了新回复";
                message = "有人回复了你的评论，点击后台或站点继续查看。";
                subject = "blog-lite：评论收到新回复";
            }
            case "COMMENT_APPROVED" -> {
                title = "你的评论已通过审核";
                message = "你发表的评论已经通过审核，现在可以公开展示。";
                subject = "blog-lite：评论审核通过";
            }
            default -> {
                title = "blog-lite 评论通知";
                message = "你有一条新的评论通知。";
                subject = "blog-lite 评论通知";
            }
        }

        Map<String, Object> variables = new HashMap<>();
        variables.put("title", title);
        variables.put("message", message);
        variables.put("recipientName", recipient.getNickname());
        variables.put("articleTitle", article.getTitle());
        variables.put("commentContent", truncate(
                comment.getContent(),
                MAX_CONTENT_PREVIEW
        ));
        variables.put("notificationType", event.notificationType());

        return new EmailContent(subject, variables);
    }

    private String truncate(String value, int maxLength) {
        if (value == null) {
            return "";
        }
        return value.length() <= maxLength
                ? value
                : value.substring(0, maxLength) + "...";
    }

    private record EmailContent(
            String subject,
            Map<String, Object> variables
    ) {
    }
}