package com.example.bloglite.service.impl;

import com.example.bloglite.dto.ArticleCommentListDTO;
import com.example.bloglite.dto.ArticleReplyDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ArticleCommentMapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 评论公开查询服务
 */
@Service
public class ArticleCommentQueryService {

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

    public ArticleCommentQueryService(
            ArticleCommentMapper articleCommentMapper,
            ArticleCommentPermissionService permissionService
    ) {
        this.articleCommentMapper = articleCommentMapper;
        this.permissionService = permissionService;
    }

    /**
     * 分页查询指定文章下的公开顶级评论
     */
    public PageResultDTO<ArticleCommentListDTO> listPublicComments(
            Long articleId,
            long current,
            long size
    ) {
        if (articleId == null || articleId < 1) {
            throw new BizException(40000, "文章 ID 不正确");
        }

        permissionService.requirePublishedArticle(
                articleId,
                "文章不存在"
        );

        current = normalizeCurrent(current);
        size = normalizeSize(size);

        long offset = (current - 1) * size;

        List<ArticleCommentListDTO> records =
                articleCommentMapper.selectApprovedTopLevelComments(
                        articleId,
                        offset,
                        size
                );

        long count =
                articleCommentMapper.countApprovedTopLevelComments(
                        articleId
                );

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    /**
     * 分页查询某个顶级评论下的公开回复
     */
    public PageResultDTO<ArticleReplyDTO> listPublicReplies(
            Long topLevelCommentId,
            long current,
            long size
    ) {
        if (topLevelCommentId == null
                || topLevelCommentId < 1) {
            throw new BizException(40000, "评论 ID 不正确");
        }

        ArticleComment rootComment =
                permissionService.requireApprovedRootComment(
                        topLevelCommentId,
                        "评论不存在"
                );

        permissionService.requirePublishedArticle(
                rootComment.getArticleId(),
                "文章不存在"
        );

        current = normalizeCurrent(current);
        size = normalizeSize(size);

        long offset = (current - 1) * size;

        List<ArticleReplyDTO> records =
                articleCommentMapper.selectApprovedReplies(
                        topLevelCommentId,
                        offset,
                        size
                );

        long count =
                articleCommentMapper.countApprovedReplies(
                        topLevelCommentId
                );

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
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