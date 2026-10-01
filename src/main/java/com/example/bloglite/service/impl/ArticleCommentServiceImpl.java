package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.ArticleCommentListDTO;
import com.example.bloglite.dto.ArticleReplyDTO;
import com.example.bloglite.dto.CreateCommentRequest;
import com.example.bloglite.dto.CreateReplyRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.enums.ArticleCommentStatus;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ArticleCommentMapper;
import com.example.bloglite.mapper.ArticleMapper;
import com.example.bloglite.service.ArticleCommentService;
import com.example.bloglite.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章评论业务实现
 */
@Service
public class ArticleCommentServiceImpl
        implements ArticleCommentService {

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

    /**
     * 评论内容最大长度
     */
    private static final int COMMENT_CONTENT_MAX_LENGTH = 1000;

    /**
     * 默认分页大小
     */
    private static final long DEFAULT_SIZE = 10;

    /**
     * 最大分页大小
     */
    private static final long MAX_SIZE = 50;

    private final ArticleCommentMapper articleCommentMapper;

    private final ArticleMapper articleMapper;

    public ArticleCommentServiceImpl(
            ArticleCommentMapper articleCommentMapper,
            ArticleMapper articleMapper
    ) {
        this.articleCommentMapper = articleCommentMapper;
        this.articleMapper = articleMapper;
    }

    @Override
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

        /*
         * 只允许对公开且未删除的文章发表评论。
         * 草稿、不存在和已删除文章统一按不可评论处理。
         */
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
            throw new BizException(
                    40400,
                    "文章不存在或不可评论"
            );
        }

        // 评论用户必须从 JWT 认证上下文获取，不能由请求体指定
        UserDTO currentUser = UserContext.getCurrentUser();

        LocalDateTime now = LocalDateTime.now();

        ArticleComment articleComment = new ArticleComment();
        articleComment.setArticleId(articleId);
        articleComment.setUserId(currentUser.getId());
        articleComment.setParentId(null);
        articleComment.setReplyToUserId(null);
        articleComment.setContent(content);
        articleComment.setStatus(
                ArticleCommentStatus.PENDING.getCode()
        );
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

        return toDTO(articleComment);
    }

    @Override
    public PageResultDTO<ArticleCommentListDTO> listPublicComments(
            Long articleId,
            long current,
            long size
    ) {
        if (articleId == null || articleId < 1) {
            throw new BizException(40000, "文章 ID 不正确");
        }

        /*
         * 评论列表属于公开接口，但仍然要先确认文章存在、
         * 已发布且没有软删除。
         */
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
            throw new BizException(40400, "文章不存在");
        }

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

    @Override
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

        /*
         * targetComment 是用户实际点击回复的那条评论，
         * 它可以是顶级评论，也可以是某条回复。
         */
        ArticleComment targetComment =
                articleCommentMapper.selectById(targetCommentId);

        if (!isApprovedAndNotDeleted(targetComment)) {
            throw new BizException(
                    40400,
                    "评论不存在或不可回复"
            );
        }

        /*
         * 所有回复都必须挂到顶级评论下面。
         * 如果目标本身就是顶级评论，它就是所属顶级评论；
         * 如果目标是一条回复，则继续查询它的顶级评论。
         */
        ArticleComment rootComment =
                targetComment.getParentId() == null
                        ? targetComment
                        : articleCommentMapper.selectById(
                        targetComment.getParentId()
                );

        if (!isApprovedAndNotDeleted(rootComment)
                || rootComment.getParentId() != null) {
            throw new BizException(
                    40400,
                    "所属顶级评论不存在或不可回复"
            );
        }

        /*
         * 回复所属的文章也必须保持公开且未删除。
         */
        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .eq(
                                Article::getId,
                                rootComment.getArticleId()
                        )
                        .eq(
                                Article::getStatus,
                                ARTICLE_STATUS_PUBLISHED
                        )
                        .eq(Article::getIsDeleted, NOT_DELETED)
        );

        if (article == null) {
            throw new BizException(
                    40400,
                    "文章不存在或不可评论"
            );
        }

        UserDTO currentUser = UserContext.getCurrentUser();

        LocalDateTime now = LocalDateTime.now();

        ArticleComment reply = new ArticleComment();
        reply.setArticleId(rootComment.getArticleId());
        reply.setUserId(currentUser.getId());
        reply.setParentId(rootComment.getId());

        /*
         * 记录实际被回复的用户。
         * 回复回复时，仍然只保存顶级评论 ID，不形成三级结构。
         */
        reply.setReplyToUserId(targetComment.getUserId());
        reply.setContent(content);
        reply.setStatus(ArticleCommentStatus.PENDING.getCode());
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

        return toDTO(reply);
    }

    @Override
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
                articleCommentMapper.selectById(topLevelCommentId);

        /*
         * 回复列表的入口必须是一条审核通过的顶级评论，
         * 不能把某条回复 ID 当成新的列表根节点。
         */
        if (!isApprovedAndNotDeleted(rootComment)
                || rootComment.getParentId() != null) {
            throw new BizException(40400, "评论不存在");
        }

        Article article = articleMapper.selectOne(
                new LambdaQueryWrapper<Article>()
                        .eq(
                                Article::getId,
                                rootComment.getArticleId()
                        )
                        .eq(
                                Article::getStatus,
                                ARTICLE_STATUS_PUBLISHED
                        )
                        .eq(Article::getIsDeleted, NOT_DELETED)
        );

        if (article == null) {
            throw new BizException(40400, "文章不存在");
        }

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

    @Transactional
    @Override
    public void deleteOwnComment(Long commentId) {
        if (commentId == null || commentId < 1) {
            throw new BizException(40000, "评论 ID 不正确");
        }

        UserDTO currentUser = UserContext.getCurrentUser();

        ArticleComment comment =
                articleCommentMapper.selectById(commentId);

        if (comment == null) {
            throw new BizException(40400, "评论不存在");
        }

        // 本课只允许评论作者删除自己的评论
        if (!currentUser.getId().equals(comment.getUserId())) {
            throw new BizException(
                    40300,
                    "无权删除他人的评论"
            );
        }

        /*
         * 已经删除的评论再次删除时直接返回成功，
         * 让 DELETE 操作具有稳定的幂等行为。
         */
        if (isDeleted(comment)) {
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

        /*
         * 影响行数大于 1 说明数据库主键或条件出现异常，
         * 正常情况下按主键更新最多只会影响一行。
         */
        if (affectedRows > 1) {
            throw new BizException(
                    50000,
                    "删除评论失败，请稍后重试"
            );
        }

        /*
         * 删除顶级评论时，同时软删除它下面的全部回复。
         * 删除回复时，只删除回复自身。
         */
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
     * 判断评论是否已通过审核且未被软删除
     */
    private boolean isApprovedAndNotDeleted(
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
    private boolean isDeleted(ArticleComment comment) {
        return Integer.valueOf(DELETED)
                .equals(comment.getIsDeleted());
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

    /**
     * 将实体转换为返回 DTO
     */
    private ArticleCommentDTO toDTO(
            ArticleComment articleComment
    ) {
        ArticleCommentDTO dto = new ArticleCommentDTO();
        dto.setId(articleComment.getId());
        dto.setArticleId(articleComment.getArticleId());
        dto.setUserId(articleComment.getUserId());
        dto.setParentId(articleComment.getParentId());
        dto.setReplyToUserId(
                articleComment.getReplyToUserId()
        );
        dto.setContent(articleComment.getContent());
        dto.setStatus(articleComment.getStatus());
        dto.setCreatedAt(articleComment.getCreatedAt());

        return dto;
    }
}