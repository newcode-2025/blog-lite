package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台接口资源树节点
 */
@Data
public class AdminResourceDTO {

    private Long id;

    private String resourceName;

    private String urlPattern;

    private String httpMethod;

    private Long parentId;

    private Integer isAnonymous;

    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<AdminResourceDTO> children = new ArrayList<>();
}
