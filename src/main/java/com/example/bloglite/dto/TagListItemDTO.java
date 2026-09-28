package com.example.bloglite.dto;

import lombok.Data;

/**
 * 标签列表数据
 */
@Data
public class TagListItemDTO {

    private Long id;

    private String name;

    private Long articleCount;
}