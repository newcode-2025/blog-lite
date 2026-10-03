package com.example.bloglite.service;

import com.example.bloglite.dto.AdminRoleCreateRequest;
import com.example.bloglite.dto.AdminRoleDTO;
import com.example.bloglite.dto.AdminRoleOptionDTO;
import com.example.bloglite.dto.AdminRoleUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;

import java.util.List;

/**
 * 后台角色业务接口
 */
public interface AdminRoleService {

    /**
     * 分页查询角色
     */
    PageResultDTO<AdminRoleDTO> listRoles(
            String keywords,
            Integer status,
            long current,
            long size
    );

    /**
     * 查询启用角色选项
     */
    List<AdminRoleOptionDTO> listRoleOptions();

    /**
     * 查询角色详情
     */
    AdminRoleDTO getRoleDetail(Long roleId);

    /**
     * 新增角色
     */
    AdminRoleDTO createRole(AdminRoleCreateRequest request);

    /**
     * 编辑角色
     */
    AdminRoleDTO updateRole(
            Long roleId,
            AdminRoleUpdateRequest request
    );

    /**
     * 删除未被使用的普通角色
     */
    void deleteRole(Long roleId);
}
