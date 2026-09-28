package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ArticleCardDTO {
    private Long id;

    private String title;

    private String summary;

    private String coverUrl;

    private String categoryName;

    private List<TagDTO> tags;

    private Integer isTop;

    private Integer isFeatured;

    private LocalDateTime publishedAt;
}
