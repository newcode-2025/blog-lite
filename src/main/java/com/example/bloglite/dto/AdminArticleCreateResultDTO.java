package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 后台新建文章返回结果
 */
@Data
@AllArgsConstructor
public class AdminArticleCreateResultDTO {

    private Long id;

    private String title;

    private Integer status;

    private LocalDateTime publishedAt;

    private List<Long> tagIds;
}