package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

/**
 * 当前登录用户角色与权限信息
 */
@Data
@AllArgsConstructor
public class CurrentUserRoleDTO {

    private Long userId;

    private String username;

    /**
     * 不含 ROLE_ 前缀的角色编码
     */
    private List<String> roleCodes;

    /**
     * Spring Security 中保存的完整权限标识
     */
    private List<String> authorities;
}
