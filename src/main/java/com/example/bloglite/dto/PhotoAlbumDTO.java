package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 相册返回数据
 */
@Data
public class PhotoAlbumDTO {

    private Long id;

    private String name;

    private String description;

    private String coverUrl;

    private Integer sortOrder;

    private Integer status;

    private Long photoCount;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}