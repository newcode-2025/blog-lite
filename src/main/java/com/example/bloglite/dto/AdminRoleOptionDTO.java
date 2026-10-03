package com.example.bloglite.dto;

import lombok.Data;

/**
 * 可分配角色选项
 */
@Data
public class AdminRoleOptionDTO {

    private Long id;

    private String roleCode;

    private String roleName;

    private Integer status;
}
