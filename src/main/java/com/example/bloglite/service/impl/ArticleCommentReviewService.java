package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.ArticleCommentReviewDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ReviewCommentRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.enums.ArticleCommentStatus;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ArticleCommentMapper;
import com.example.bloglite.service.CommentNotificationService;
import com.example.bloglite.util.UserContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 评论审核服务
 */
@Slf4j
@Service
public class ArticleCommentReviewService {

    /**
     * 未删除
     */
    private static final int NOT_DELETED = 0;

    /**
     * 默认分页大小
     */
    private static final long DEFAULT_SIZE = 10;

    /**
     * 最大分页大小
     */
    private static final long MAX_SIZE = 50;

    private final ArticleCommentMapper articleCommentMapper;
    private final ArticleCommentPermissionService permissionService;
    private final CommentNotificationService commentNotificationService;

    public ArticleCommentReviewService(
            ArticleCommentMapper articleCommentMapper,
            ArticleCommentPermissionService permissionService,
            CommentNotificationService commentNotificationService
    ) {
        this.articleCommentMapper = articleCommentMapper;
        this.permissionService = permissionService;
        this.commentNotificationService = commentNotificationService;
    }

    /**
     * 文章作者分页查询自己文章的评论审核列表
     */
    public PageResultDTO<ArticleCommentReviewDTO> listReviewComments(
            Long articleId,
            Integer status,
            long current,
            long size
    ) {
        if (articleId == null || articleId < 1) {
            throw new BizException(40000, "文章 ID 不正确");
        }

        if (status == null) {
            status = ArticleCommentStatus.PENDING.getCode();
        }

        if (!isValidStatus(status)) {
            throw new BizException(40000, "评论状态不正确");
        }

        UserDTO currentUser = UserContext.getCurrentUser();

        Article article = permissionService.requireArticle(
                articleId,
                "文章不存在"
        );

        permissionService.requireArticleAuthor(
                article,
                currentUser.getId(),
                "无权查看该文章的评论审核列表"
        );

        current = normalizeCurrent(current);
        size = normalizeSize(size);

        long offset = (current - 1) * size;

        List<ArticleCommentReviewDTO> records =
                articleCommentMapper.selectReviewComments(
                        articleId,
                        status,
                        offset,
                        size
                );

        long count =
                articleCommentMapper.countReviewComments(
                        articleId,
                        status
                );

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    /**
     * 文章作者审核单条评论
     */
    @Transactional
    public void reviewComment(
            Long commentId,
            ReviewCommentRequest request
    ) {
        if (commentId == null || commentId < 1) {
            throw new BizException(40000, "评论 ID 不正确");
        }

        if (request == null || request.getStatus() == null) {
            throw new BizException(40000, "审核状态不能为空");
        }

        Integer status = request.getStatus();

        if (!isReviewResultStatus(status)) {
            throw new BizException(
                    40000,
                    "审核状态只能是 1 或 2"
            );
        }

        UserDTO currentUser = UserContext.getCurrentUser();

        ArticleComment comment =
                permissionService.requireNotDeletedComment(
                        commentId,
                        "评论不存在"
                );

        Article article = permissionService.requireArticle(
                comment.getArticleId(),
                "评论所属文章不存在"
        );

        permissionService.requireArticleAuthor(
                article,
                currentUser.getId(),
                "无权审核该评论"
        );

        /*
         * 回复要通过审核时，它所属的顶级评论必须已经通过审核。
         * 否则回复虽然自身状态为已通过，仍然无法在公开列表展示。
         */
        if (status.equals(
                ArticleCommentStatus.APPROVED.getCode()
        ) && comment.getParentId() != null) {
            ArticleComment rootComment =
                    articleCommentMapper.selectById(
                            comment.getParentId()
                    );

            if (!permissionService.isApprovedAndNotDeleted(
                    rootComment
            ) || rootComment.getParentId() != null) {
                throw new BizException(
                        40900,
                        "所属顶级评论尚未通过审核"
                );
            }
        }

        LocalDateTime now = LocalDateTime.now();

        LambdaUpdateWrapper<ArticleComment> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(ArticleComment::getId, commentId)
                .eq(ArticleComment::getIsDeleted, NOT_DELETED)
                .set(ArticleComment::getStatus, status)
                .set(ArticleComment::getUpdatedAt, now);

        int affectedRows = articleCommentMapper.update(
                null,
                updateWrapper
        );

        if (affectedRows > 1) {
            throw new BizException(
                    50000,
                    "评论审核失败，请稍后重试"
            );
        }

        if (affectedRows == 1 && status.equals(
                ArticleCommentStatus.APPROVED.getCode()
        )) {
            comment.setStatus(status);
            comment.setUpdatedAt(now);
            notifyApprovedSafely(comment, article);
        }
    }

    /**
     * 判断审核列表查询的状态是否合法
     */
    private boolean isValidStatus(Integer status) {
        return Integer.valueOf(
                ArticleCommentStatus.PENDING.getCode()
        ).equals(status)
                || Integer.valueOf(
                ArticleCommentStatus.APPROVED.getCode()
        ).equals(status)
                || Integer.valueOf(
                ArticleCommentStatus.REJECTED.getCode()
        ).equals(status);
    }

    /**
     * 判断是否为评论审核结果状态
     */
    private boolean isReviewResultStatus(Integer status) {
        return Integer.valueOf(
                ArticleCommentStatus.APPROVED.getCode()
        ).equals(status)
                || Integer.valueOf(
                ArticleCommentStatus.REJECTED.getCode()
        ).equals(status);
    }

    private void notifyApprovedSafely(
            ArticleComment comment,
            Article article
    ) {
        try {
            commentNotificationService.notifyCommentApproved(
                    comment,
                    article
            );
        } catch (RuntimeException exception) {
            log.error(
                    "提交评论审核通知失败，commentId=" + comment.getId(),
                    exception
            );
        }
    }

    /**
     * 规范化当前页码
     */
    private long normalizeCurrent(long current) {
        return current < 1 ? 1 : current;
    }

    /**
     * 规范化分页大小
     */
    private long normalizeSize(long size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            return MAX_SIZE;
        }

        return size;
    }
}