package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.enums.ArticleCommentStatus;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ArticleCommentMapper;
import com.example.bloglite.mapper.ArticleMapper;
import org.springframework.stereotype.Service;

import java.util.Objects;

/**
 * 评论权限和公共校验服务
 */
@Service
public class ArticleCommentPermissionService {

    /**
     * 公开文章状态
     */
    private static final int ARTICLE_STATUS_PUBLISHED = 1;

    /**
     * 未删除
     */
    private static final int NOT_DELETED = 0;

    /**
     * 已软删除
     */
    private static final int DELETED = 1;

    private final ArticleMapper articleMapper;
    private final ArticleCommentMapper articleCommentMapper;

    public ArticleCommentPermissionService(
            ArticleMapper articleMapper,
            ArticleCommentMapper articleCommentMapper
    ) {
        this.articleMapper = articleMapper;
        this.articleCommentMapper = articleCommentMapper;
    }

    /**
     * 查询公开且未删除的文章
     */
    public Article requirePublishedArticle(
            Long articleId,
            String message
    ) {
        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .eq(Article::getId, articleId)
                        .eq(
                                Article::getStatus,
                                ARTICLE_STATUS_PUBLISHED
                        )
                        .eq(Article::getIsDeleted, NOT_DELETED)
        );

        if (article == null) {
            throw new BizException(40400, message);
        }

        return article;
    }

    /**
     * 根据 ID 查询文章
     */
    public Article requireArticle(Long articleId, String message) {
        Article article = articleMapper.selectById(articleId);

        if (article == null) {
            throw new BizException(40400, message);
        }

        return article;
    }

    /**
     * 查询未删除的评论
     */
    public ArticleComment requireNotDeletedComment(
            Long commentId,
            String message
    ) {
        ArticleComment comment =
                articleCommentMapper.selectById(commentId);

        if (comment == null || isDeleted(comment)) {
            throw new BizException(40400, message);
        }

        return comment;
    }

    /**
     * 查询审核通过且未删除的顶级评论
     */
    public ArticleComment requireApprovedRootComment(
            Long commentId,
            String message
    ) {
        ArticleComment comment =
                articleCommentMapper.selectById(commentId);

        if (!isApprovedAndNotDeleted(comment)
                || comment.getParentId() != null) {
            throw new BizException(40400, message);
        }

        return comment;
    }

    /**
     * 校验文章作者权限
     */
    public void requireArticleAuthor(
            Article article,
            Long currentUserId,
            String message
    ) {
        if (!Objects.equals(article.getAuthorId(), currentUserId)) {
            throw new BizException(40300, message);
        }
    }

    /**
     * 校验删除评论权限
     * 评论作者可以删除自己的评论，文章作者可以删除文章下的评论
     */
    public void requireDeletePermission(
            Long currentUserId,
            ArticleComment comment
    ) {
        if (isCommentAuthor(currentUserId, comment)) {
            return;
        }

        Article article = articleMapper.selectById(
                comment.getArticleId()
        );

        if (article == null) {
            throw new BizException(
                    40400,
                    "评论所属文章不存在"
            );
        }

        if (!isArticleAuthor(currentUserId, article)) {
            throw new BizException(
                    40300,
                    "无权删除该评论"
            );
        }
    }

    /**
     * 判断评论是否为当前用户发表
     */
    public boolean isCommentAuthor(
            Long currentUserId,
            ArticleComment comment
    ) {
        return Objects.equals(currentUserId, comment.getUserId());
    }

    /**
     * 判断文章是否为当前用户所有
     */
    public boolean isArticleAuthor(
            Long currentUserId,
            Article article
    ) {
        return Objects.equals(currentUserId, article.getAuthorId());
    }

    /**
     * 判断评论是否已通过审核且未被软删除
     */
    public boolean isApprovedAndNotDeleted(
            ArticleComment comment
    ) {
        return comment != null
                && Integer.valueOf(
                ArticleCommentStatus.APPROVED.getCode()
        ).equals(comment.getStatus())
                && Integer.valueOf(NOT_DELETED)
                .equals(comment.getIsDeleted());
    }

    /**
     * 判断评论是否已经被软删除
     */
    public boolean isDeleted(ArticleComment comment) {
        return Integer.valueOf(DELETED)
                .equals(comment.getIsDeleted());
    }
}