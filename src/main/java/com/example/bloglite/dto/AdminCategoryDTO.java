package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台分类数据
 */
@Data
public class AdminCategoryDTO {

    private Long id;

    private String name;

    private Integer sortOrder;

    /**
     * 关联文章数量，包含草稿、密码文章和软删除文章
     */
    private Long articleCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}