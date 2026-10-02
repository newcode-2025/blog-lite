package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 后台标签数据
 */
@Data
public class AdminTagDTO {

    private Long id;

    private String name;

    /**
     * 关联文章数量，包含草稿、密码文章和软删除文章
     */
    private Long articleCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}