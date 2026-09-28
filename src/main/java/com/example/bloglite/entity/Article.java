package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article")
public class Article {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String title;

    private String summary;

    private String content;

    private String coverUrl;

    private Long categoryId;

    private Long authorId;

    private Integer status;

    private String accessPassword;

    private Integer isTop;

    private Integer isFeatured;

    private Long viewCount;

    private Integer isDeleted;

    private LocalDateTime publishedAt;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
