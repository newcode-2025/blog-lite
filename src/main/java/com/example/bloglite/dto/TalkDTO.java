package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 说说返回数据
 */
@Data
public class TalkDTO {

    private Long id;

    private Long userId;

    private String nickname;

    private String avatarUrl;

    private String content;

    private List<String> images;

    private Integer isTop;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}