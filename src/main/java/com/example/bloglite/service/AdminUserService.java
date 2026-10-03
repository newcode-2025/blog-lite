package com.example.bloglite.service;

import com.example.bloglite.dto.AdminUserDTO;
import com.example.bloglite.dto.AdminUserRoleUpdateRequest;
import com.example.bloglite.dto.AdminUserStatusUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;

/**
 * 后台用户业务接口
 */
public interface AdminUserService {

    /**
     * 分页查询用户
     */
    PageResultDTO<AdminUserDTO> listUsers(
            String keywords,
            Integer status,
            long current,
            long size
    );

    /**
     * 查询用户详情
     */
    AdminUserDTO getUserDetail(Long userId);

    /**
     * 修改用户启用状态
     */
    AdminUserDTO updateUserStatus(
            Long userId,
            AdminUserStatusUpdateRequest request
    );

    /**
     * 替换用户角色
     */
    AdminUserDTO updateUserRoles(
            Long userId,
            AdminUserRoleUpdateRequest request
    );
}
