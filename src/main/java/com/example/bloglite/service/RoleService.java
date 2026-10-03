package com.example.bloglite.service;

import java.util.List;

/**
 * 角色业务接口
 */
public interface RoleService {

    /**
     * 查询用户当前有效的角色编码
     *
     * @param userId 用户 ID
     * @return 启用的角色编码列表
     */
    List<String> listEnabledRoleCodesByUserId(Long userId);
}
