package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 相册图片实体
 */
@Data
@TableName("photo")
public class Photo {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long albumId;

    private String name;

    private String description;

    private String photoUrl;

    private Integer sortOrder;

    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}