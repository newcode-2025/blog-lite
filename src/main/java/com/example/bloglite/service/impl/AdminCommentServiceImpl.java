package com.example.bloglite.service.impl;

import com.example.bloglite.dto.AdminCommentDTO;
import com.example.bloglite.dto.BatchReviewCommentsRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ReviewCommentRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.Article;
import com.example.bloglite.enums.ArticleCommentStatus;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.AdminCommentMapper;
import com.example.bloglite.service.AdminCommentService;
import com.example.bloglite.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 后台评论业务实现
 */
@Service
public class AdminCommentServiceImpl implements AdminCommentService {

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;
    private static final int MAX_BATCH_SIZE = 100;

    private final AdminCommentMapper adminCommentMapper;
    private final ArticleCommentReviewService reviewService;
    private final ArticleCommentPermissionService permissionService;

    public AdminCommentServiceImpl(
            AdminCommentMapper adminCommentMapper,
            ArticleCommentReviewService reviewService,
            ArticleCommentPermissionService permissionService
    ) {
        this.adminCommentMapper = adminCommentMapper;
        this.reviewService = reviewService;
        this.permissionService = permissionService;
    }

    /**
     * 分页查询当前用户文章下的评论
     */
    @Override
    public PageResultDTO<AdminCommentDTO> listComments(
            Long articleId,
            Integer status,
            String keywords,
            long current,
            long size
    ) {
        if (articleId != null && articleId < 1) {
            throw new BizException(40000, "文章 ID 不正确");
        }

        validateListStatus(status);

        UserDTO currentUser = UserContext.getCurrentUser();

        if (articleId != null) {
            Article article = permissionService.requireArticle(
                    articleId,
                    "文章不存在"
            );

            permissionService.requireArticleAuthor(
                    article,
                    currentUser.getId(),
                    "无权查看该文章的评论"
            );
        }

        current = normalizeCurrent(current);
        size = normalizeSize(size);

        String normalizedKeywords = normalizeKeywords(keywords);

        long offset = (current - 1) * size;

        List<AdminCommentDTO> records =
                adminCommentMapper.selectAdminComments(
                        currentUser.getId(),
                        articleId,
                        status,
                        normalizedKeywords,
                        offset,
                        size
                );

        long count = adminCommentMapper.countAdminComments(
                currentUser.getId(),
                articleId,
                status,
                normalizedKeywords
        );

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    /**
     * 批量审核评论
     */
    @Transactional
    @Override
    public void batchReviewComments(
            BatchReviewCommentsRequest request
    ) {
        if (request == null) {
            throw new BizException(40000, "审核请求不能为空");
        }

        validateReviewStatus(request.getStatus());

        List<Long> commentIds =
                normalizeCommentIds(request.getCommentIds());

        ReviewCommentRequest singleRequest =
                new ReviewCommentRequest();

        singleRequest.setStatus(request.getStatus());

        /*
         * 循环调用已经验收的单条审核服务。
         * 外层事务保证任意一条失败时整批回滚。
         */
        for (Long commentId : commentIds) {
            reviewService.reviewComment(commentId, singleRequest);
        }
    }

    /**
     * 校验列表查询状态
     */
    private void validateListStatus(Integer status) {
        if (status == null) {
            return;
        }

        if (!Integer.valueOf(
                ArticleCommentStatus.PENDING.getCode()
        ).equals(status)
                && !Integer.valueOf(
                ArticleCommentStatus.APPROVED.getCode()
        ).equals(status)
                && !Integer.valueOf(
                ArticleCommentStatus.REJECTED.getCode()
        ).equals(status)) {
            throw new BizException(
                    40000,
                    "评论状态不正确"
            );
        }
    }

    /**
     * 校验批量审核目标状态
     */
    private void validateReviewStatus(Integer status) {
        if (status == null) {
            throw new BizException(
                    40000,
                    "审核状态不能为空"
            );
        }

        if (!Integer.valueOf(
                ArticleCommentStatus.APPROVED.getCode()
        ).equals(status)
                && !Integer.valueOf(
                ArticleCommentStatus.REJECTED.getCode()
        ).equals(status)) {
            throw new BizException(
                    40000,
                    "审核状态只能是 1 或 2"
            );
        }
    }

    /**
     * 校验并规范化评论 ID 列表
     */
    private List<Long> normalizeCommentIds(
            List<Long> commentIds
    ) {
        if (commentIds == null || commentIds.isEmpty()) {
            throw new BizException(
                    40000,
                    "评论 ID 列表不能为空"
            );
        }

        if (commentIds.size() > MAX_BATCH_SIZE) {
            throw new BizException(
                    40000,
                    "一次最多审核 100 条评论"
            );
        }

        List<Long> normalizedCommentIds =
                new ArrayList<>(commentIds.size());

        Set<Long> uniqueCommentIds = new HashSet<>();

        for (Long commentId : commentIds) {
            if (commentId == null || commentId < 1) {
                throw new BizException(
                        40000,
                        "评论 ID 必须大于 0"
                );
            }

            if (!uniqueCommentIds.add(commentId)) {
                throw new BizException(
                        40000,
                        "评论 ID 不能重复"
                );
            }

            normalizedCommentIds.add(commentId);
        }

        return normalizedCommentIds;
    }

    /**
     * 规范化搜索关键词
     */
    private String normalizeKeywords(String keywords) {
        if (keywords == null) {
            return null;
        }

        String normalizedKeywords = keywords.trim();

        return normalizedKeywords.isEmpty()
                ? null
                : normalizedKeywords;
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