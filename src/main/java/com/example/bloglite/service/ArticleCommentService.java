package com.example.bloglite.service;

import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.ArticleCommentListDTO;
import com.example.bloglite.dto.ArticleCommentReviewDTO;
import com.example.bloglite.dto.ArticleReplyDTO;
import com.example.bloglite.dto.CreateCommentRequest;
import com.example.bloglite.dto.CreateReplyRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ReviewCommentRequest;

/**
 * 文章评论业务接口
 */
public interface ArticleCommentService {

    /**
     * 在指定文章下发表顶级评论
     */
    ArticleCommentDTO createTopLevelComment(
            Long articleId,
            CreateCommentRequest request
    );

    /**
     * 分页查询指定文章下的公开顶级评论
     */
    PageResultDTO<ArticleCommentListDTO> listPublicComments(
            Long articleId,
            long current,
            long size
    );

    /**
     * 回复指定评论
     */
    ArticleCommentDTO createReply(
            Long targetCommentId,
            CreateReplyRequest request
    );

    /**
     * 分页查询某个顶级评论下的公开回复
     */
    PageResultDTO<ArticleReplyDTO> listPublicReplies(
            Long topLevelCommentId,
            long current,
            long size
    );

    /**
     * 删除评论
     *
     * 评论作者可以删除自己的评论；
     * 文章作者可以删除自己文章下的评论。
     */
    void deleteComment(Long commentId);

    /**
     * 文章作者分页查询自己文章的评论审核列表
     */
    PageResultDTO<ArticleCommentReviewDTO> listReviewComments(
            Long articleId,
            Integer status,
            long current,
            long size
    );

    /**
     * 文章作者审核单条评论
     */
    void reviewComment(
            Long commentId,
            ReviewCommentRequest request
    );
}