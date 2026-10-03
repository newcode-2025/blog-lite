package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RBAC 接口资源实体
 */
@Data
@TableName("api_resource")
public class ApiResource {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String resourceName;

    /**
     * Ant 风格 URL 规则；分组根节点为空
     */
    private String urlPattern;

    /**
     * HTTP 方法；分组根节点为空
     */
    private String httpMethod;

    private Long parentId;

    /**
     * 0 需要授权，1 允许匿名
     */
    private Integer isAnonymous;

    /**
     * 1 启用，0 禁用
     */
    private Integer status;

    private Integer sortOrder;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
