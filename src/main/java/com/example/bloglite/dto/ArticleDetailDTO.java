package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 文章详情数据
 */
@Data
public class ArticleDetailDTO {

    private Long id;

    private String title;

    private String summary;

    private String content;

    private String coverUrl;

    private String categoryName;

    private List<TagDTO> tags;

    private Integer isTop;

    private Integer isFeatured;

    private Long viewCount;

    private LocalDateTime publishedAt;
}