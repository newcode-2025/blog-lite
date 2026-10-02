package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台文章列表项
 */
@Data
public class AdminArticleListItemDTO {

    private Long id;

    private String title;

    private String summary;

    private Long categoryId;

    private String categoryName;

    private Integer status;

    private Integer isTop;

    private Integer isFeatured;

    private Long viewCount;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}