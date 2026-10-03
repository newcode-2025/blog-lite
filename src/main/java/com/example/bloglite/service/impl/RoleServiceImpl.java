package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.entity.Role;
import com.example.bloglite.entity.UserRole;
import com.example.bloglite.mapper.RoleMapper;
import com.example.bloglite.mapper.UserRoleMapper;
import com.example.bloglite.service.RoleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 角色业务实现
 */
@Service
public class RoleServiceImpl implements RoleService {

    private static final int STATUS_ENABLED = 1;

    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    public RoleServiceImpl(
            RoleMapper roleMapper,
            UserRoleMapper userRoleMapper
    ) {
        this.roleMapper = roleMapper;
        this.userRoleMapper = userRoleMapper;
    }

    @Override
    public List<String> listEnabledRoleCodesByUserId(Long userId) {
        if (userId == null || userId < 1) {
            return List.of();
        }

        List<UserRole> userRoles = userRoleMapper.selectList(
                Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getUserId, userId)
                        .orderByAsc(UserRole::getRoleId)
        );

        if (userRoles.isEmpty()) {
            return List.of();
        }

        List<Long> roleIds = userRoles.stream()
                .map(UserRole::getRoleId)
                .distinct()
                .toList();

        List<Role> roles = roleMapper.selectList(
                Wrappers.<Role>lambdaQuery()
                        .in(Role::getId, roleIds)
                        .eq(Role::getStatus, STATUS_ENABLED)
                        .orderByAsc(Role::getId)
        );

        return roles.stream()
                .map(Role::getRoleCode)
                .filter(roleCode -> roleCode != null && !roleCode.isBlank())
                .distinct()
                .toList();
    }
}
