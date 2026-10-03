package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.dto.AdminUserDTO;
import com.example.bloglite.dto.AdminUserRoleDTO;
import com.example.bloglite.dto.AdminUserRoleUpdateRequest;
import com.example.bloglite.dto.AdminUserStatusUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.Role;
import com.example.bloglite.entity.UserAccount;
import com.example.bloglite.entity.UserRole;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.AdminUserMapper;
import com.example.bloglite.mapper.RoleMapper;
import com.example.bloglite.mapper.UserRoleMapper;
import com.example.bloglite.service.AdminUserService;
import com.example.bloglite.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 后台用户业务实现
 */
@Service
public class AdminUserServiceImpl implements AdminUserService {

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_ENABLED = 1;
    private static final int ROLE_STATUS_ENABLED = 1;
    private static final String ADMIN_ROLE_CODE = "ADMIN";

    private final AdminUserMapper adminUserMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMapper roleMapper;

    public AdminUserServiceImpl(
            AdminUserMapper adminUserMapper,
            UserRoleMapper userRoleMapper,
            RoleMapper roleMapper
    ) {
        this.adminUserMapper = adminUserMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public PageResultDTO<AdminUserDTO> listUsers(
            String keywords,
            Integer status,
            long current,
            long size
    ) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        validateStatus(status);

        String normalizedKeywords = normalizeKeywords(keywords);
        long offset = (current - 1) * size;

        List<UserAccount> users = adminUserMapper.selectAdminUsers(
                normalizedKeywords,
                status,
                offset,
                size
        );

        Map<Long, List<AdminUserRoleDTO>> roleMap =
                loadRoleMap(users.stream().map(UserAccount::getId).toList());

        List<AdminUserDTO> records = users.stream()
                .map(user -> toDTO(
                        user,
                        roleMap.getOrDefault(user.getId(), List.of())
                ))
                .toList();

        long count = adminUserMapper.countAdminUsers(
                normalizedKeywords,
                status
        );

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    @Override
    public AdminUserDTO getUserDetail(Long userId) {
        UserAccount user = getUserOrThrow(userId);
        return toDTO(user, loadRolesByUserId(userId));
    }

    @Transactional
    @Override
    public AdminUserDTO updateUserStatus(
            Long userId,
            AdminUserStatusUpdateRequest request
    ) {
        UserAccount user = getUserOrThrow(userId);
        validateStatus(request.getStatus());

        if (!request.getStatus().equals(user.getStatus())) {
            if (request.getStatus() == STATUS_DISABLED) {
                Long currentUserId = UserContext.getCurrentUser().getId();

                if (userId.equals(currentUserId)) {
                    throw new BizException(
                            40900,
                            "不能禁用当前登录账号"
                    );
                }

                ensureNotLastEnabledAdmin(userId);
            }

            LocalDateTime now = LocalDateTime.now();

            LambdaUpdateWrapper<UserAccount> updateWrapper =
                    new LambdaUpdateWrapper<>();

            updateWrapper
                    .eq(UserAccount::getId, userId)
                    .set(UserAccount::getStatus, request.getStatus())
                    .set(UserAccount::getUpdatedAt, now);

            int updatedRows = adminUserMapper.update(null, updateWrapper);

            if (updatedRows < 1) {
                throw new BizException(
                        50000,
                        "用户状态更新失败，请稍后重试"
                );
            }
        }

        return getUserDetail(userId);
    }

    @Transactional
    @Override
    public AdminUserDTO updateUserRoles(
            Long userId,
            AdminUserRoleUpdateRequest request
    ) {
        UserAccount user = getUserOrThrow(userId);
        List<Long> roleIds = normalizeRoleIds(request.getRoleIds());

        List<Role> roles = loadEnabledRoles(roleIds);
        boolean currentlyAdmin = hasEnabledAdminRole(userId);

        boolean newRolesContainAdmin = roles.stream()
                .anyMatch(role -> ADMIN_ROLE_CODE.equals(role.getRoleCode()));

        if (currentlyAdmin && !newRolesContainAdmin) {
            Long currentUserId = UserContext.getCurrentUser().getId();

            if (userId.equals(currentUserId)) {
                throw new BizException(
                        40900,
                        "不能移除当前登录账号的管理员角色"
                );
            }

            ensureNotLastEnabledAdmin(userId);
        }

        userRoleMapper.delete(
                Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getUserId, userId)
        );

        for (Long roleId : roleIds) {
            UserRole userRole = new UserRole();
            userRole.setUserId(userId);
            userRole.setRoleId(roleId);

            int insertedRows = userRoleMapper.insert(userRole);

            if (insertedRows != 1) {
                throw new BizException(
                        50000,
                        "用户角色更新失败，请稍后重试"
                );
            }
        }

        return getUserDetail(userId);
    }

    private Map<Long, List<AdminUserRoleDTO>> loadRoleMap(
            List<Long> userIds
    ) {
        if (userIds.isEmpty()) {
            return Map.of();
        }

        return adminUserMapper.selectRolesByUserIds(userIds)
                .stream()
                .collect(Collectors.groupingBy(
                        AdminUserRoleDTO::getUserId,
                        LinkedHashMap::new,
                        Collectors.toList()
                ));
    }

    private List<AdminUserRoleDTO> loadRolesByUserId(Long userId) {
        return adminUserMapper.selectRolesByUserIds(List.of(userId));
    }

    private boolean hasEnabledAdminRole(Long userId) {
        return loadRolesByUserId(userId)
                .stream()
                .anyMatch(role ->
                        ADMIN_ROLE_CODE.equals(role.getRoleCode())
                                && Integer.valueOf(ROLE_STATUS_ENABLED)
                                .equals(role.getStatus())
                );
    }

    private void ensureNotLastEnabledAdmin(Long userId) {
        if (!hasEnabledAdminRole(userId)) {
            return;
        }

        long otherAdminCount =
                adminUserMapper.countEnabledUsersByRoleCode(
                        ADMIN_ROLE_CODE,
                        userId
                );

        if (otherAdminCount < 1) {
            throw new BizException(
                    40900,
                    "不能禁用或移除最后一个启用管理员"
            );
        }
    }

    private List<Role> loadEnabledRoles(List<Long> roleIds) {
        if (roleIds.isEmpty()) {
            return List.of();
        }

        List<Role> roles = roleMapper.selectBatchIds(roleIds);

        if (roles.size() != roleIds.size()) {
            throw new BizException(40400, "存在不存在的角色");
        }

        boolean hasDisabledRole = roles.stream()
                .anyMatch(role ->
                        !Integer.valueOf(ROLE_STATUS_ENABLED)
                                .equals(role.getStatus())
                );

        if (hasDisabledRole) {
            throw new BizException(40000, "不能分配已禁用角色");
        }

        return roles;
    }

    private UserAccount getUserOrThrow(Long userId) {
        validateUserId(userId);

        UserAccount user = adminUserMapper.selectById(userId);

        if (user == null) {
            throw new BizException(40400, "用户不存在");
        }

        return user;
    }

    private AdminUserDTO toDTO(
            UserAccount user,
            List<AdminUserRoleDTO> roles
    ) {
        AdminUserDTO dto = new AdminUserDTO();
        dto.setId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setNickname(user.getNickname());
        dto.setEmail(user.getEmail());
        dto.setAvatarUrl(user.getAvatarUrl());
        dto.setStatus(user.getStatus());
        dto.setLastLoginAt(user.getLastLoginAt());
        dto.setCreatedAt(user.getCreatedAt());
        dto.setUpdatedAt(user.getUpdatedAt());
        dto.setRoles(roles);
        return dto;
    }

    private List<Long> normalizeRoleIds(List<Long> roleIds) {
        if (roleIds == null) {
            throw new BizException(40000, "角色 ID 列表不能为空");
        }

        if (roleIds.size() > MAX_SIZE) {
            throw new BizException(40000, "一次最多分配 50 个角色");
        }

        List<Long> normalizedRoleIds = new ArrayList<>(roleIds.size());
        Set<Long> uniqueRoleIds = new HashSet<>();

        for (Long roleId : roleIds) {
            if (roleId == null || roleId < 1) {
                throw new BizException(40000, "角色 ID 必须大于 0");
            }

            if (!uniqueRoleIds.add(roleId)) {
                throw new BizException(40000, "角色 ID 不能重复");
            }

            normalizedRoleIds.add(roleId);
        }

        return normalizedRoleIds;
    }

    private String normalizeKeywords(String keywords) {
        if (keywords == null) {
            return null;
        }

        String normalizedKeywords = keywords.trim();
        return normalizedKeywords.isEmpty() ? null : normalizedKeywords;
    }

    private void validateUserId(Long userId) {
        if (userId == null || userId < 1) {
            throw new BizException(40000, "用户 ID 不正确");
        }
    }

    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }

        if (status != STATUS_DISABLED && status != STATUS_ENABLED) {
            throw new BizException(40000, "用户状态只能是 0 或 1");
        }
    }

    private long normalizeCurrent(long current) {
        return current < 1 ? 1 : current;
    }

    private long normalizeSize(long size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }

        return Math.min(size, MAX_SIZE);
    }
}
