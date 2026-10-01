package com.example.bloglite.service;

import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.dto.CreateCommentRequest;

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
}