package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("article_tag")
public class ArticleTag {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long articleId;

    private Long tagId;

    private LocalDateTime createdAt;
}
