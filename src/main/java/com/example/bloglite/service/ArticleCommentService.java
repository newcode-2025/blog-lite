package com.example.bloglite.service;

import com.example.bloglite.dto.*;

/**
 * 文章评论业务接口
 */
public interface ArticleCommentService {

    /**
     * 在指定文章下发表顶级评论
     *
     * @param articleId 文章 ID
     * @param request   评论请求
     * @return 新建的评论
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
     * 删除当前用户自己的评论
     */
    void deleteOwnComment(Long commentId);
}