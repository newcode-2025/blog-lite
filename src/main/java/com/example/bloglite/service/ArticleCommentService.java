package com.example.bloglite.service;

import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.ArticleCommentListDTO;
import com.example.bloglite.dto.CreateCommentRequest;
import com.example.bloglite.dto.PageResultDTO;

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
}