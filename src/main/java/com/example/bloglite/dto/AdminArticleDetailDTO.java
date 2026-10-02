package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台文章编辑详情
 */
@Data
public class AdminArticleDetailDTO {

    private Long id;

    private String title;

    private String summary;

    private String content;

    private String coverUrl;

    private Long categoryId;

    private String categoryName;

    private Integer status;

    /**
     * 是否已经设置访问密码，不返回密码明文
     */
    private Boolean hasAccessPassword;

    private Integer isTop;

    private Integer isFeatured;

    private Long viewCount;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<TagDTO> tags;
}