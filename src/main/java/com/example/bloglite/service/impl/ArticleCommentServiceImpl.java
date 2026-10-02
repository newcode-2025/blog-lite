package com.example.bloglite.service.impl;

import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.ArticleCommentListDTO;
import com.example.bloglite.dto.ArticleCommentReviewDTO;
import com.example.bloglite.dto.ArticleReplyDTO;
import com.example.bloglite.dto.CreateCommentRequest;
import com.example.bloglite.dto.CreateReplyRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ReviewCommentRequest;
import com.example.bloglite.service.ArticleCommentService;
import org.springframework.stereotype.Service;

/**
 * 文章评论业务门面
 * 保持原有对外接口不变，将具体职责委托给内部服务
 */
@Service
public class ArticleCommentServiceImpl
        implements ArticleCommentService {

    private final ArticleCommentCommandService commandService;
    private final ArticleCommentQueryService queryService;
    private final ArticleCommentReviewService reviewService;

    public ArticleCommentServiceImpl(
            ArticleCommentCommandService commandService,
            ArticleCommentQueryService queryService,
            ArticleCommentReviewService reviewService
    ) {
        this.commandService = commandService;
        this.queryService = queryService;
        this.reviewService = reviewService;
    }

    /**
     * 在指定文章下发表顶级评论
     */
    @Override
    public ArticleCommentDTO createTopLevelComment(
            Long articleId,
            CreateCommentRequest request
    ) {
        return commandService.createTopLevelComment(
                articleId,
                request
        );
    }

    /**
     * 分页查询指定文章下的公开顶级评论
     */
    @Override
    public PageResultDTO<ArticleCommentListDTO> listPublicComments(
            Long articleId,
            long current,
            long size
    ) {
        return queryService.listPublicComments(
                articleId,
                current,
                size
        );
    }

    /**
     * 回复指定评论
     */
    @Override
    public ArticleCommentDTO createReply(
            Long targetCommentId,
            CreateReplyRequest request
    ) {
        return commandService.createReply(
                targetCommentId,
                request
        );
    }

    /**
     * 分页查询某个顶级评论下的公开回复
     */
    @Override
    public PageResultDTO<ArticleReplyDTO> listPublicReplies(
            Long topLevelCommentId,
            long current,
            long size
    ) {
        return queryService.listPublicReplies(
                topLevelCommentId,
                current,
                size
        );
    }

    /**
     * 删除评论
     */
    @Override
    public void deleteComment(Long commentId) {
        commandService.deleteComment(commentId);
    }

    /**
     * 文章作者分页查询自己文章的评论审核列表
     */
    @Override
    public PageResultDTO<ArticleCommentReviewDTO> listReviewComments(
            Long articleId,
            Integer status,
            long current,
            long size
    ) {
        return reviewService.listReviewComments(
                articleId,
                status,
                current,
                size
        );
    }

    /**
     * 文章作者审核单条评论
     */
    @Override
    public void reviewComment(
            Long commentId,
            ReviewCommentRequest request
    ) {
        reviewService.reviewComment(commentId, request);
    }
}