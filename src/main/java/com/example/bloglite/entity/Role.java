package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * RBAC 角色实体
 */
@Data
@TableName("rbac_role")
public class Role {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 稳定角色编码，例如 ADMIN、USER
     */
    private String roleCode;

    private String roleName;

    private String description;

    /**
     * 1 启用，0 禁用
     */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
