package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 友情链接返回数据
 */
@Data
public class FriendLinkDTO {

    private Long id;

    private String name;

    private String avatarUrl;

    private String address;

    private String description;

    private Integer sortOrder;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}