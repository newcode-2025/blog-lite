package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.dto.AdminRoleCreateRequest;
import com.example.bloglite.dto.AdminRoleDTO;
import com.example.bloglite.dto.AdminRoleOptionDTO;
import com.example.bloglite.dto.AdminRoleUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.entity.Role;
import com.example.bloglite.entity.RoleMenu;
import com.example.bloglite.entity.RoleResource;
import com.example.bloglite.entity.UserRole;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.AdminRoleMapper;
import com.example.bloglite.mapper.RoleMenuMapper;
import com.example.bloglite.mapper.RoleResourceMapper;
import com.example.bloglite.mapper.UserRoleMapper;
import com.example.bloglite.service.AdminRoleService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * 后台角色业务实现
 */
@Service
public class AdminRoleServiceImpl implements AdminRoleService {

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_ENABLED = 1;

    private static final String ADMIN_ROLE_CODE = "ADMIN";
    private static final String USER_ROLE_CODE = "USER";

    private static final Set<String> SYSTEM_ROLE_CODES =
            Set.of(ADMIN_ROLE_CODE, USER_ROLE_CODE);

    private static final Pattern ROLE_CODE_PATTERN =
            Pattern.compile("^[A-Z][A-Z0-9_]{1,49}$");

    private final AdminRoleMapper adminRoleMapper;
    private final UserRoleMapper userRoleMapper;
    private final RoleMenuMapper roleMenuMapper;
    private final RoleResourceMapper roleResourceMapper;

    public AdminRoleServiceImpl(
            AdminRoleMapper adminRoleMapper,
            UserRoleMapper userRoleMapper,
            RoleMenuMapper roleMenuMapper,
            RoleResourceMapper roleResourceMapper
    ) {
        this.adminRoleMapper = adminRoleMapper;
        this.userRoleMapper = userRoleMapper;
        this.roleMenuMapper = roleMenuMapper;
        this.roleResourceMapper = roleResourceMapper;
    }

    @Override
    public PageResultDTO<AdminRoleDTO> listRoles(
            String keywords,
            Integer status,
            long current,
            long size
    ) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);
        validateOptionalStatus(status);

        String normalizedKeywords = normalizeKeywords(keywords);
        long offset = (current - 1) * size;

        List<AdminRoleDTO> records = adminRoleMapper.selectAdminRoles(
                        normalizedKeywords,
                        status,
                        offset,
                        size
                )
                .stream()
                .map(this::toDTO)
                .toList();

        long count = adminRoleMapper.countAdminRoles(
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
    public List<AdminRoleOptionDTO> listRoleOptions() {
        return adminRoleMapper.selectEnabledRoleOptions();
    }

    @Override
    public AdminRoleDTO getRoleDetail(Long roleId) {
        return toDTO(getRoleOrThrow(roleId));
    }

    @Transactional
    @Override
    public AdminRoleDTO createRole(AdminRoleCreateRequest request) {
        String roleCode = normalizeRoleCode(request.getRoleCode());
        String roleName = normalizeRoleName(request.getRoleName());
        String description = normalizeDescription(request.getDescription());
        Integer status = validateRequiredStatus(request.getStatus());

        ensureRoleCodeAvailable(roleCode, null);
        ensureRoleNameAvailable(roleName, null);

        LocalDateTime now = LocalDateTime.now();

        Role role = new Role();
        role.setRoleCode(roleCode);
        role.setRoleName(roleName);
        role.setDescription(description);
        role.setStatus(status);
        role.setCreatedAt(now);
        role.setUpdatedAt(now);

        int insertedRows;

        try {
            insertedRows = adminRoleMapper.insert(role);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "角色编码或角色名称已存在",
                    exception
            );
        }

        if (insertedRows != 1 || role.getId() == null) {
            throw new BizException(
                    50000,
                    "角色创建失败，请稍后重试"
            );
        }

        return getRoleDetail(role.getId());
    }

    @Transactional
    @Override
    public AdminRoleDTO updateRole(
            Long roleId,
            AdminRoleUpdateRequest request
    ) {
        Role existingRole = getRoleOrThrow(roleId);

        String roleName = normalizeRoleName(request.getRoleName());
        String description = normalizeDescription(request.getDescription());
        Integer status = validateRequiredStatus(request.getStatus());

        if (ADMIN_ROLE_CODE.equals(existingRole.getRoleCode())
                && status == STATUS_DISABLED) {
            throw new BizException(
                    40900,
                    "系统管理员角色不能禁用"
            );
        }

        ensureRoleNameAvailable(roleName, roleId);

        Role updateRole = new Role();
        updateRole.setId(roleId);
        updateRole.setRoleName(roleName);
        updateRole.setDescription(description);
        updateRole.setStatus(status);
        updateRole.setUpdatedAt(LocalDateTime.now());

        int updatedRows;

        try {
            updatedRows = adminRoleMapper.updateById(updateRole);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "角色名称已存在",
                    exception
            );
        }

        if (updatedRows != 1) {
            throw new BizException(
                    50000,
                    "角色更新失败，请稍后重试"
            );
        }

        return getRoleDetail(roleId);
    }

    @Transactional
    @Override
    public void deleteRole(Long roleId) {
        Role role = getRoleOrThrow(roleId);

        if (SYSTEM_ROLE_CODES.contains(role.getRoleCode())) {
            throw new BizException(
                    40900,
                    "系统内置角色不能删除"
            );
        }

        long userRoleCount = userRoleMapper.selectCount(
                Wrappers.<UserRole>lambdaQuery()
                        .eq(UserRole::getRoleId, roleId)
        );

        if (userRoleCount > 0) {
            throw new BizException(
                    40900,
                    "角色已分配给用户，不能删除"
            );
        }

        long roleMenuCount = roleMenuMapper.selectCount(
                Wrappers.<RoleMenu>lambdaQuery()
                        .eq(RoleMenu::getRoleId, roleId)
        );

        if (roleMenuCount > 0) {
            throw new BizException(
                    40900,
                    "角色已分配菜单，不能删除"
            );
        }

        long roleResourceCount = roleResourceMapper.selectCount(
                Wrappers.<RoleResource>lambdaQuery()
                        .eq(RoleResource::getRoleId, roleId)
        );

        if (roleResourceCount > 0) {
            throw new BizException(
                    40900,
                    "角色已分配接口资源，不能删除"
            );
        }

        int deletedRows = adminRoleMapper.deleteById(roleId);

        if (deletedRows != 1) {
            throw new BizException(
                    50000,
                    "角色删除失败，请稍后重试"
            );
        }
    }

    private Role getRoleOrThrow(Long roleId) {
        validateRoleId(roleId);

        Role role = adminRoleMapper.selectById(roleId);

        if (role == null) {
            throw new BizException(40400, "角色不存在");
        }

        return role;
    }

    private void ensureRoleCodeAvailable(
            String roleCode,
            Long excludeRoleId
    ) {
        long count = adminRoleMapper.selectCount(
                Wrappers.<Role>lambdaQuery()
                        .eq(Role::getRoleCode, roleCode)
                        .ne(
                                excludeRoleId != null,
                                Role::getId,
                                excludeRoleId
                        )
        );

        if (count > 0) {
            throw new BizException(40900, "角色编码已存在");
        }
    }

    private void ensureRoleNameAvailable(
            String roleName,
            Long excludeRoleId
    ) {
        long count = adminRoleMapper.selectCount(
                Wrappers.<Role>lambdaQuery()
                        .eq(Role::getRoleName, roleName)
                        .ne(
                                excludeRoleId != null,
                                Role::getId,
                                excludeRoleId
                        )
        );

        if (count > 0) {
            throw new BizException(40900, "角色名称已存在");
        }
    }

    private AdminRoleDTO toDTO(Role role) {
        AdminRoleDTO dto = new AdminRoleDTO();
        dto.setId(role.getId());
        dto.setRoleCode(role.getRoleCode());
        dto.setRoleName(role.getRoleName());
        dto.setDescription(role.getDescription());
        dto.setStatus(role.getStatus());
        dto.setCreatedAt(role.getCreatedAt());
        dto.setUpdatedAt(role.getUpdatedAt());
        return dto;
    }

    private String normalizeRoleCode(String roleCode) {
        if (roleCode == null || roleCode.trim().isEmpty()) {
            throw new BizException(40000, "角色编码不能为空");
        }

        String normalizedCode = roleCode
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!ROLE_CODE_PATTERN.matcher(normalizedCode).matches()) {
            throw new BizException(
                    40000,
                    "角色编码必须以字母开头，只能包含大写字母、数字和下划线，长度为 2 到 50"
            );
        }

        return normalizedCode;
    }

    private String normalizeRoleName(String roleName) {
        if (roleName == null || roleName.trim().isEmpty()) {
            throw new BizException(40000, "角色名称不能为空");
        }

        String normalizedName = roleName.trim();

        if (normalizedName.length() > 50) {
            throw new BizException(40000, "角色名称不能超过 50 个字符");
        }

        return normalizedName;
    }

    private String normalizeDescription(String description) {
        if (description == null) {
            return null;
        }

        String normalizedDescription = description.trim();

        if (normalizedDescription.isEmpty()) {
            return null;
        }

        if (normalizedDescription.length() > 255) {
            throw new BizException(40000, "角色说明不能超过 255 个字符");
        }

        return normalizedDescription;
    }

    private String normalizeKeywords(String keywords) {
        if (keywords == null) {
            return null;
        }

        String normalizedKeywords = keywords.trim();
        return normalizedKeywords.isEmpty() ? null : normalizedKeywords;
    }

    private Integer validateRequiredStatus(Integer status) {
        if (status == null) {
            throw new BizException(40000, "角色状态不能为空");
        }

        validateOptionalStatus(status);
        return status;
    }

    private void validateOptionalStatus(Integer status) {
        if (status == null) {
            return;
        }

        if (status != STATUS_DISABLED && status != STATUS_ENABLED) {
            throw new BizException(40000, "角色状态只能是 0 或 1");
        }
    }

    private void validateRoleId(Long roleId) {
        if (roleId == null || roleId < 1) {
            throw new BizException(40000, "角色 ID 不正确");
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
