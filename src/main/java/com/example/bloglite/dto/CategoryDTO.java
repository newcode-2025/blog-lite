package com.example.bloglite.dto;

import lombok.Data;

/**
 * 分类列表数据
 */
@Data
public class CategoryDTO {

    private Long id;

    private String name;

    private Integer sortOrder;

    private Long articleCount;
}