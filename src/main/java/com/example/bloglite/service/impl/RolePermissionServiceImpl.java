package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.dto.RoleMenuAssignRequest;
import com.example.bloglite.dto.RolePermissionDTO;
import com.example.bloglite.dto.RoleResourceAssignRequest;
import com.example.bloglite.entity.ApiResource;
import com.example.bloglite.entity.Menu;
import com.example.bloglite.entity.Role;
import com.example.bloglite.entity.RoleMenu;
import com.example.bloglite.entity.RoleResource;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ApiResourceMapper;
import com.example.bloglite.mapper.MenuMapper;
import com.example.bloglite.mapper.RoleMapper;
import com.example.bloglite.mapper.RoleMenuMapper;
import com.example.bloglite.mapper.RoleResourceMapper;
import com.example.bloglite.service.RolePermissionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

/**
 * 角色权限分配业务实现
 */
@Service
public class RolePermissionServiceImpl implements RolePermissionService {

    private static final int STATUS_ENABLED = 1;
    private static final int MAX_ASSIGNMENT_SIZE = 500;

    private final RoleMapper roleMapper;
    private final MenuMapper menuMapper;
    private final ApiResourceMapper apiResourceMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleResourceMapper roleResourceMapper;

    public RolePermissionServiceImpl(
            RoleMapper roleMapper,
            MenuMapper menuMapper,
            ApiResourceMapper apiResourceMapper,
            RoleMenuMapper roleMenuMapper,
            RoleResourceMapper roleResourceMapper
    ) {
        this.roleMapper = roleMapper;
        this.menuMapper = menuMapper;
        this.apiResourceMapper = apiResourceMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.roleResourceMapper = roleResourceMapper;
    }

    @Override
    public RolePermissionDTO getRolePermissions(Long roleId) {
        return loadRolePermissions(getAssignableRoleOrThrow(roleId));
    }

    @Transactional
    @Override
    public RolePermissionDTO assignRoleMenus(
            Long roleId,
            RoleMenuAssignRequest request
    ) {
        Role role = getAssignableRoleOrThrow(roleId);

        if (request == null || request.getMenuIds() == null) {
            throw new BizException(40000, "菜单 ID 列表不能为空");
        }

        List<Long> menuIds = normalizeIds(
                request.getMenuIds(),
                "菜单 ID"
        );
        ensureMenusExist(menuIds);

        roleMenuMapper.delete(
                Wrappers.<RoleMenu>lambdaQuery()
                        .eq(RoleMenu::getRoleId, roleId)
        );

        LocalDateTime now = LocalDateTime.now();

        for (Long menuId : menuIds) {
            RoleMenu roleMenu = new RoleMenu();
            roleMenu.setRoleId(roleId);
            roleMenu.setMenuId(menuId);
            roleMenu.setCreatedAt(now);

            int insertedRows = roleMenuMapper.insert(roleMenu);

            if (insertedRows != 1) {
                throw new BizException(
                        50000,
                        "角色菜单分配失败，请稍后重试"
                );
            }
        }

        return loadRolePermissions(role);
    }

    @Transactional
    @Override
    public RolePermissionDTO assignRoleResources(
            Long roleId,
            RoleResourceAssignRequest request
    ) {
        Role role = getAssignableRoleOrThrow(roleId);

        if (request == null || request.getResourceIds() == null) {
            throw new BizException(40000, "接口资源 ID 列表不能为空");
        }

        List<Long> resourceIds = normalizeIds(
                request.getResourceIds(),
                "接口资源 ID"
        );
        ensureResourcesExist(resourceIds);

        roleResourceMapper.delete(
                Wrappers.<RoleResource>lambdaQuery()
                        .eq(RoleResource::getRoleId, roleId)
        );

        LocalDateTime now = LocalDateTime.now();

        for (Long resourceId : resourceIds) {
            RoleResource roleResource = new RoleResource();
            roleResource.setRoleId(roleId);
            roleResource.setResourceId(resourceId);
            roleResource.setCreatedAt(now);

            int insertedRows = roleResourceMapper.insert(roleResource);

            if (insertedRows != 1) {
                throw new BizException(
                        50000,
                        "角色接口资源分配失败，请稍后重试"
                );
            }
        }

        return loadRolePermissions(role);
    }

    private RolePermissionDTO loadRolePermissions(Role role) {
        List<Long> menuIds = roleMenuMapper.selectList(
                        Wrappers.<RoleMenu>lambdaQuery()
                                .eq(RoleMenu::getRoleId, role.getId())
                                .orderByAsc(RoleMenu::getMenuId)
                )
                .stream()
                .map(RoleMenu::getMenuId)
                .toList();

        List<Long> resourceIds = roleResourceMapper.selectList(
                        Wrappers.<RoleResource>lambdaQuery()
                                .eq(RoleResource::getRoleId, role.getId())
                                .orderByAsc(RoleResource::getResourceId)
                )
                .stream()
                .map(RoleResource::getResourceId)
                .toList();

        RolePermissionDTO dto = new RolePermissionDTO();
        dto.setRoleId(role.getId());
        dto.setRoleCode(role.getRoleCode());
        dto.setRoleName(role.getRoleName());
        dto.setMenuIds(menuIds);
        dto.setResourceIds(resourceIds);
        return dto;
    }

    private Role getAssignableRoleOrThrow(Long roleId) {
        validateRoleId(roleId);

        Role role = roleMapper.selectById(roleId);

        if (role == null) {
            throw new BizException(40400, "角色不存在");
        }

        if (!Integer.valueOf(STATUS_ENABLED).equals(role.getStatus())) {
            throw new BizException(40900, "已禁用角色不能分配权限");
        }

        return role;
    }

    private void ensureMenusExist(List<Long> menuIds) {
        if (menuIds.isEmpty()) {
            return;
        }

        List<Menu> menus = menuMapper.selectBatchIds(menuIds);

        if (menus.size() != menuIds.size()) {
            throw new BizException(40400, "存在不存在的菜单");
        }
    }

    private void ensureResourcesExist(List<Long> resourceIds) {
        if (resourceIds.isEmpty()) {
            return;
        }

        List<ApiResource> resources =
                apiResourceMapper.selectBatchIds(resourceIds);

        if (resources.size() != resourceIds.size()) {
            throw new BizException(40400, "存在不存在的接口资源");
        }
    }

    private List<Long> normalizeIds(
            List<Long> rawIds,
            String fieldName
    ) {
        if (rawIds.size() > MAX_ASSIGNMENT_SIZE) {
            throw new BizException(
                    40000,
                    "一次最多分配 " + MAX_ASSIGNMENT_SIZE + " 个" + fieldName
            );
        }

        Set<Long> uniqueIds = new LinkedHashSet<>();

        for (Long id : rawIds) {
            if (id == null || id < 1) {
                throw new BizException(
                        40000,
                        fieldName + "必须大于 0"
                );
            }

            if (!uniqueIds.add(id)) {
                throw new BizException(
                        40000,
                        fieldName + "不能重复"
                );
            }
        }

        return uniqueIds.stream().sorted().toList();
    }

    private void validateRoleId(Long roleId) {
        if (roleId == null || roleId < 1) {
            throw new BizException(40000, "角色 ID 不正确");
        }
    }
}
