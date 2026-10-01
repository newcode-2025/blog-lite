package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.ArticleCommentListDTO;
import com.example.bloglite.dto.CreateCommentRequest;
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

        if (current < 1) {
            current = 1;
        }

        if (size < 1) {
            size = DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

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