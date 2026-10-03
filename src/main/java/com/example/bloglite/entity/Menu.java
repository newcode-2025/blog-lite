package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RBAC 后台菜单实体
 */
@Data
@TableName("rbac_menu")
public class Menu {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String path;

    private String component;

    private String icon;

    private Integer sortOrder;

    private Long parentId;

    /**
     * 0 显示，1 隐藏
     */
    private Integer isHidden;

    /**
     * 1 启用，0 禁用
     */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
