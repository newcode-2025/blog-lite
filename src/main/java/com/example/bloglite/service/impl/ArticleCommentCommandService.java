package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.CreateCommentRequest;
import com.example.bloglite.dto.CreateReplyRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.enums.ArticleCommentStatus;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ArticleCommentMapper;
import com.example.bloglite.service.CommentNotificationService;
import com.example.bloglite.service.SiteService;
import com.example.bloglite.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 评论命令服务
 */
@Slf4j
@Service
public class ArticleCommentCommandService {

    /**
     * 未删除
     */
    private static final int NOT_DELETED = 0;

    /**
     * 已软删除
     */
    private static final int DELETED = 1;

    /**
     * 评论内容最大长度
     */
    private static final int COMMENT_CONTENT_MAX_LENGTH = 1000;

    private final ArticleCommentMapper articleCommentMapper;
    private final ArticleCommentPermissionService permissionService;
    private final ArticleCommentConverter commentConverter;
    private final SiteService siteService;
    private final CommentNotificationService commentNotificationService;

    public ArticleCommentCommandService(
            ArticleCommentMapper articleCommentMapper,
            ArticleCommentPermissionService permissionService,
            ArticleCommentConverter commentConverter,
            SiteService siteService,
            CommentNotificationService commentNotificationService
    ) {
        this.articleCommentMapper = articleCommentMapper;
        this.permissionService = permissionService;
        this.commentConverter = commentConverter;
        this.siteService = siteService;
        this.commentNotificationService = commentNotificationService;
    }

    /**
     * 在指定文章下发表顶级评论
     */
    @Transactional
    public ArticleCommentDTO createTopLevelComment(
            Long articleId,
            CreateCommentRequest request
    ) {
        if (articleId == null) {
            throw new BizException(40000, "文章 ID 不能为空");
        }

        if (request == null) {
            throw new BizException(40000, "评论请求不能为空");
        }

        String content = normalizeContent(request.getContent());

        Article article = permissionService.requirePublishedArticle(
                articleId,
                "文章不存在或不可评论"
        );

        // 评论用户必须从 JWT 认证上下文获取，不能由请求体指定
        UserDTO currentUser = UserContext.getCurrentUser();

        LocalDateTime now = LocalDateTime.now();

        ArticleComment articleComment = new ArticleComment();
        articleComment.setArticleId(articleId);
        articleComment.setUserId(currentUser.getId());
        articleComment.setParentId(null);
        articleComment.setReplyToUserId(null);
        articleComment.setContent(content);
        articleComment.setStatus(resolveInitialStatus());
        articleComment.setIsDeleted(NOT_DELETED);
        articleComment.setCreatedAt(now);
        articleComment.setUpdatedAt(now);

        int affectedRows = articleCommentMapper.insert(
                articleComment
        );

        if (affectedRows != 1) {
            throw new BizException(
                    50000,
                    "评论发表失败，请稍后重试"
            );
        }

        notifyTopLevelCommentSafely(articleComment, article);
        return commentConverter.toDTO(articleComment);
    }

    /**
     * 回复指定评论
     */
    @Transactional
    public ArticleCommentDTO createReply(
            Long targetCommentId,
            CreateReplyRequest request
    ) {
        if (targetCommentId == null || targetCommentId < 1) {
            throw new BizException(40000, "目标评论 ID 不正确");
        }

        if (request == null) {
            throw new BizException(40000, "回复请求不能为空");
        }

        String content = normalizeContent(request.getContent());

        ArticleComment targetComment =
                articleCommentMapper.selectById(targetCommentId);

        if (!permissionService.isApprovedAndNotDeleted(
                targetComment
        )) {
            throw new BizException(
                    40400,
                    "评论不存在或不可回复"
            );
        }

        ArticleComment rootComment =
                targetComment.getParentId() == null
                        ? targetComment
                        : articleCommentMapper.selectById(
                        targetComment.getParentId()
                );

        if (!permissionService.isApprovedAndNotDeleted(
                rootComment
        ) || rootComment.getParentId() != null) {
            throw new BizException(
                    40400,
                    "所属顶级评论不存在或不可回复"
            );
        }

        Article article = permissionService.requirePublishedArticle(
                rootComment.getArticleId(),
                "文章不存在或不可评论"
        );

        UserDTO currentUser = UserContext.getCurrentUser();

        LocalDateTime now = LocalDateTime.now();

        ArticleComment reply = new ArticleComment();
        reply.setArticleId(rootComment.getArticleId());
        reply.setUserId(currentUser.getId());
        reply.setParentId(rootComment.getId());
        reply.setReplyToUserId(targetComment.getUserId());
        reply.setContent(content);
        reply.setStatus(resolveInitialStatus());
        reply.setIsDeleted(NOT_DELETED);
        reply.setCreatedAt(now);
        reply.setUpdatedAt(now);

        int affectedRows = articleCommentMapper.insert(reply);

        if (affectedRows != 1) {
            throw new BizException(
                    50000,
                    "回复发表失败，请稍后重试"
            );
        }

        notifyReplySafely(reply, article);
        return commentConverter.toDTO(reply);
    }

    /**
     * 删除评论
     */
    @Transactional
    public void deleteComment(Long commentId) {
        if (commentId == null || commentId < 1) {
            throw new BizException(40000, "评论 ID 不正确");
        }

        UserDTO currentUser = UserContext.getCurrentUser();

        ArticleComment comment =
                articleCommentMapper.selectById(commentId);

        if (comment == null) {
            throw new BizException(40400, "评论不存在");
        }

        permissionService.requireDeletePermission(
                currentUser.getId(),
                comment
        );

        if (permissionService.isDeleted(comment)) {
            return;
        }

        LocalDateTime now = LocalDateTime.now();

        LambdaUpdateWrapper<ArticleComment> commentUpdateWrapper =
                new LambdaUpdateWrapper<>();

        commentUpdateWrapper
                .eq(ArticleComment::getId, commentId)
                .eq(ArticleComment::getIsDeleted, NOT_DELETED)
                .set(ArticleComment::getIsDeleted, DELETED)
                .set(ArticleComment::getUpdatedAt, now);

        int affectedRows = articleCommentMapper.update(
                null,
                commentUpdateWrapper
        );

        if (affectedRows > 1) {
            throw new BizException(
                    50000,
                    "删除评论失败，请稍后重试"
            );
        }

        if (comment.getParentId() == null) {
            LambdaUpdateWrapper<ArticleComment> replyUpdateWrapper =
                    new LambdaUpdateWrapper<>();

            replyUpdateWrapper
                    .eq(
                            ArticleComment::getParentId,
                            commentId
                    )
                    .eq(
                            ArticleComment::getIsDeleted,
                            NOT_DELETED
                    )
                    .set(
                            ArticleComment::getIsDeleted,
                            DELETED
                    )
                    .set(
                            ArticleComment::getUpdatedAt,
                            now
                    );

            articleCommentMapper.update(
                    null,
                    replyUpdateWrapper
            );
        }
    }

    /**
     * 根据网站配置决定评论初始审核状态，配置读取失败时保持默认待审核
     */
    private int resolveInitialStatus() {
        try {
            return Integer.valueOf(1).equals(
                    siteService.getConfig().getCommentReviewEnabled()
            )
                    ? ArticleCommentStatus.PENDING.getCode()
                    : ArticleCommentStatus.APPROVED.getCode();
        } catch (RuntimeException exception) {
            log.warn("读取评论审核配置失败，保持默认待审核状态", exception);
            return ArticleCommentStatus.PENDING.getCode();
        }
    }

    private void notifyTopLevelCommentSafely(
            ArticleComment comment,
            Article article
    ) {
        try {
            commentNotificationService.notifyTopLevelCommentCreated(
                    comment,
                    article
            );
        } catch (RuntimeException exception) {
            log.error(
                    "提交顶级评论通知失败，commentId=" + comment.getId(),
                    exception
            );
        }
    }

    private void notifyReplySafely(
            ArticleComment reply,
            Article article
    ) {
        try {
            commentNotificationService.notifyReplyCreated(reply, article);
        } catch (RuntimeException exception) {
            log.error(
                    "提交回复通知失败，commentId=" + reply.getId(),
                    exception
            );
        }
    }

    /**
     * 清理评论内容并校验长度
     */
    private String normalizeContent(String content) {
        if (content == null) {
            throw new BizException(40000, "评论内容不能为空");
        }

        String normalizedContent = content.trim();

        if (normalizedContent.isEmpty()) {
            throw new BizException(40000, "评论内容不能为空");
        }

        if (normalizedContent.length()
                > COMMENT_CONTENT_MAX_LENGTH) {
            throw new BizException(
                    40000,
                    "评论内容不能超过 1000 个字符"
            );
        }

        return normalizedContent;
    }
}