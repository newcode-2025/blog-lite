package com.example.bloglite.dto;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 后台菜单树节点
 */
@Data
public class AdminMenuDTO {

    private Long id;

    private String name;

    private String path;

    private String component;

    private String icon;

    private Integer sortOrder;

    private Long parentId;

    private Integer isHidden;

    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private List<AdminMenuDTO> children = new ArrayList<>();
}
