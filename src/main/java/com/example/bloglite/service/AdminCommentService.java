package com.example.bloglite.service;

import com.example.bloglite.dto.AdminCommentDTO;
import com.example.bloglite.dto.BatchReviewCommentsRequest;
import com.example.bloglite.dto.PageResultDTO;

/**
 * 后台评论业务接口
 */
public interface AdminCommentService {

    /**
     * 分页查询当前用户文章下的评论
     */
    PageResultDTO<AdminCommentDTO> listComments(
            Long articleId,
            Integer status,
            String keywords,
            long current,
            long size
    );

    /**
     * 批量审核评论
     */
    void batchReviewComments(BatchReviewCommentsRequest request);
}