package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 文章阅读排行
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArticleViewRankDTO {

    /**
     * 文章 ID
     */
    private Long id;

    /**
     * 文章标题
     */
    private String title;

    /**
     * 阅读量
     */
    private Long viewCount;
}