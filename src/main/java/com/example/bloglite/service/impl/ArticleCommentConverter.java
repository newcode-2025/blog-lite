package com.example.bloglite.service.impl;

import com.example.bloglite.dto.ArticleCommentDTO;
import com.example.bloglite.entity.ArticleComment;
import org.springframework.stereotype.Service;

/**
 * 评论 DTO 转换器
 */
@Service
public class ArticleCommentConverter {

    /**
     * 将评论实体转换为返回 DTO
     */
    public ArticleCommentDTO toDTO(ArticleComment articleComment) {
        ArticleCommentDTO dto = new ArticleCommentDTO();
        dto.setId(articleComment.getId());
        dto.setArticleId(articleComment.getArticleId());
        dto.setUserId(articleComment.getUserId());
        dto.setParentId(articleComment.getParentId());
        dto.setReplyToUserId(articleComment.getReplyToUserId());
        dto.setContent(articleComment.getContent());
        dto.setStatus(articleComment.getStatus());
        dto.setCreatedAt(articleComment.getCreatedAt());
        return dto;
    }
}