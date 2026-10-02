package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 相册图片返回数据
 */
@Data
public class PhotoDTO {

    private Long id;

    private Long albumId;

    private String name;

    private String description;

    private String photoUrl;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}