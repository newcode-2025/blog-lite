package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.dto.AdminMenuDTO;
import com.example.bloglite.dto.AdminMenuSaveRequest;
import com.example.bloglite.entity.Menu;
import com.example.bloglite.entity.RoleMenu;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.MenuMapper;
import com.example.bloglite.mapper.RoleMenuMapper;
import com.example.bloglite.service.AdminMenuService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 后台菜单业务实现
 */
@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    private static final int STATUS_DISABLED = 0;
    private static final int STATUS_ENABLED = 1;
    private static final int HIDDEN_NO = 0;
    private static final int HIDDEN_YES = 1;

    private final MenuMapper menuMapper;
    private final RoleMenuMapper roleMenuMapper;

    public AdminMenuServiceImpl(
            MenuMapper menuMapper,
            RoleMenuMapper roleMenuMapper
    ) {
        this.menuMapper = menuMapper;
        this.roleMenuMapper = roleMenuMapper;
    }

    @Override
    public List<AdminMenuDTO> listMenuTree() {
        return buildTree(loadAllMenus());
    }

    @Override
    public AdminMenuDTO getMenuDetail(Long menuId) {
        return toDTO(getMenuOrThrow(menuId));
    }

    @Transactional
    @Override
    public AdminMenuDTO createMenu(AdminMenuSaveRequest request) {
        NormalizedMenu normalizedMenu = normalizeRequest(request, null);

        ensureNameAvailable(
                normalizedMenu.name(),
                normalizedMenu.parentId(),
                null
        );

        LocalDateTime now = LocalDateTime.now();

        Menu menu = new Menu();
        menu.setName(normalizedMenu.name());
        menu.setPath(normalizedMenu.path());
        menu.setComponent(normalizedMenu.component());
        menu.setIcon(normalizedMenu.icon());
        menu.setSortOrder(normalizedMenu.sortOrder());
        menu.setParentId(normalizedMenu.parentId());
        menu.setIsHidden(normalizedMenu.isHidden());
        menu.setStatus(normalizedMenu.status());
        menu.setCreatedAt(now);
        menu.setUpdatedAt(now);

        int insertedRows;

        try {
            insertedRows = menuMapper.insert(menu);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "同一父菜单下已存在同名菜单",
                    exception
            );
        }

        if (insertedRows != 1 || menu.getId() == null) {
            throw new BizException(
                    50000,
                    "菜单创建失败，请稍后重试"
            );
        }

        return getMenuDetail(menu.getId());
    }

    @Transactional
    @Override
    public AdminMenuDTO updateMenu(
            Long menuId,
            AdminMenuSaveRequest request
    ) {
        getMenuOrThrow(menuId);
        NormalizedMenu normalizedMenu =
                normalizeRequest(request, menuId);

        ensureNameAvailable(
                normalizedMenu.name(),
                normalizedMenu.parentId(),
                menuId
        );

        Menu menu = new Menu();
        menu.setId(menuId);
        menu.setName(normalizedMenu.name());
        menu.setPath(normalizedMenu.path());
        menu.setComponent(normalizedMenu.component());
        menu.setIcon(normalizedMenu.icon());
        menu.setSortOrder(normalizedMenu.sortOrder());
        menu.setParentId(normalizedMenu.parentId());
        menu.setIsHidden(normalizedMenu.isHidden());
        menu.setStatus(normalizedMenu.status());
        menu.setUpdatedAt(LocalDateTime.now());

        int updatedRows;

        try {
            updatedRows = menuMapper.updateById(menu);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "同一父菜单下已存在同名菜单",
                    exception
            );
        }

        if (updatedRows != 1) {
            throw new BizException(
                    50000,
                    "菜单更新失败，请稍后重试"
            );
        }

        return getMenuDetail(menuId);
    }

    @Transactional
    @Override
    public void deleteMenu(Long menuId) {
        getMenuOrThrow(menuId);

        long childCount = menuMapper.selectCount(
                Wrappers.<Menu>lambdaQuery()
                        .eq(Menu::getParentId, menuId)
        );

        if (childCount > 0) {
            throw new BizException(
                    40900,
                    "菜单存在子菜单，不能删除"
            );
        }

        long roleMenuCount = roleMenuMapper.selectCount(
                Wrappers.<RoleMenu>lambdaQuery()
                        .eq(RoleMenu::getMenuId, menuId)
        );

        if (roleMenuCount > 0) {
            throw new BizException(
                    40900,
                    "菜单已分配给角色，不能删除"
            );
        }

        int deletedRows = menuMapper.deleteById(menuId);

        if (deletedRows != 1) {
            throw new BizException(
                    50000,
                    "菜单删除失败，请稍后重试"
            );
        }
    }

    private List<Menu> loadAllMenus() {
        return menuMapper.selectList(
                Wrappers.<Menu>lambdaQuery()
                        .orderByAsc(Menu::getSortOrder)
                        .orderByAsc(Menu::getId)
        );
    }

    private List<AdminMenuDTO> buildTree(List<Menu> menus) {
        Map<Long, AdminMenuDTO> menuMap = new LinkedHashMap<>();

        for (Menu menu : menus) {
            menuMap.put(menu.getId(), toDTO(menu));
        }

        List<AdminMenuDTO> roots = new ArrayList<>();

        for (Menu menu : menus) {
            AdminMenuDTO current = menuMap.get(menu.getId());

            if (menu.getParentId() == null) {
                roots.add(current);
                continue;
            }

            AdminMenuDTO parent = menuMap.get(menu.getParentId());

            if (parent == null) {
                roots.add(current);
                continue;
            }

            parent.getChildren().add(current);
        }

        return roots;
    }

    private NormalizedMenu normalizeRequest(
            AdminMenuSaveRequest request,
            Long currentMenuId
    ) {
        if (request == null) {
            throw new BizException(40000, "菜单请求不能为空");
        }

        String name = normalizeRequiredText(
                request.getName(),
                "菜单名称不能为空",
                100
        );

        String path = normalizeOptionalText(
                request.getPath(),
                "路由地址不能超过 255 个字符",
                255
        );

        String component = normalizeOptionalText(
                request.getComponent(),
                "组件标识不能超过 255 个字符",
                255
        );

        String icon = normalizeOptionalText(
                request.getIcon(),
                "菜单图标不能超过 100 个字符",
                100
        );

        Integer sortOrder = request.getSortOrder();

        if (sortOrder == null || sortOrder < 0) {
            throw new BizException(40000, "菜单排序值不能小于 0");
        }

        Long parentId = request.getParentId();
        validateParent(currentMenuId, parentId);

        Integer isHidden = validateBinaryStatus(
                request.getIsHidden(),
                "菜单隐藏状态只能是 0 或 1"
        );

        Integer status = validateBinaryStatus(
                request.getStatus(),
                "菜单状态只能是 0 或 1"
        );

        return new NormalizedMenu(
                name,
                path,
                component,
                icon,
                sortOrder,
                parentId,
                isHidden,
                status
        );
    }

    private void validateParent(Long currentMenuId, Long parentId) {
        if (parentId == null) {
            return;
        }

        if (currentMenuId != null && currentMenuId.equals(parentId)) {
            throw new BizException(40000, "父菜单不能是当前菜单");
        }

        Menu parent = menuMapper.selectById(parentId);

        if (parent == null) {
            throw new BizException(40400, "父菜单不存在");
        }

        Set<Long> visited = new HashSet<>();
        Long cursorId = parentId;

        while (cursorId != null) {
            if (currentMenuId != null && currentMenuId.equals(cursorId)) {
                throw new BizException(40900, "菜单父子关系不能形成循环");
            }

            if (!visited.add(cursorId)) {
                throw new BizException(40900, "菜单父子关系已存在循环");
            }

            Menu cursor = cursorId.equals(parentId)
                    ? parent
                    : menuMapper.selectById(cursorId);

            if (cursor == null) {
                throw new BizException(40400, "父菜单链中存在不存在的菜单");
            }

            cursorId = cursor.getParentId();
        }
    }

    private void ensureNameAvailable(
            String name,
            Long parentId,
            Long excludeMenuId
    ) {
        LambdaQueryWrapper<Menu> queryWrapper =
                Wrappers.<Menu>lambdaQuery()
                        .eq(Menu::getName, name)
                        .ne(
                                excludeMenuId != null,
                                Menu::getId,
                                excludeMenuId
                        );

        if (parentId == null) {
            queryWrapper.isNull(Menu::getParentId);
        } else {
            queryWrapper.eq(Menu::getParentId, parentId);
        }

        long count = menuMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new BizException(
                    40900,
                    "同一父菜单下已存在同名菜单"
            );
        }
    }

    private Menu getMenuOrThrow(Long menuId) {
        validateMenuId(menuId);

        Menu menu = menuMapper.selectById(menuId);

        if (menu == null) {
            throw new BizException(40400, "菜单不存在");
        }

        return menu;
    }

    private AdminMenuDTO toDTO(Menu menu) {
        AdminMenuDTO dto = new AdminMenuDTO();
        dto.setId(menu.getId());
        dto.setName(menu.getName());
        dto.setPath(menu.getPath());
        dto.setComponent(menu.getComponent());
        dto.setIcon(menu.getIcon());
        dto.setSortOrder(menu.getSortOrder());
        dto.setParentId(menu.getParentId());
        dto.setIsHidden(menu.getIsHidden());
        dto.setStatus(menu.getStatus());
        dto.setCreatedAt(menu.getCreatedAt());
        dto.setUpdatedAt(menu.getUpdatedAt());
        dto.setChildren(new ArrayList<>());
        return dto;
    }

    private String normalizeRequiredText(
            String value,
            String emptyMessage,
            int maxLength
    ) {
        if (value == null || value.trim().isEmpty()) {
            throw new BizException(40000, emptyMessage);
        }

        String normalizedValue = value.trim();

        if (normalizedValue.length() > maxLength) {
            throw new BizException(40000, emptyMessage);
        }

        return normalizedValue;
    }

    private String normalizeOptionalText(
            String value,
            String tooLongMessage,
            int maxLength
    ) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();

        if (normalizedValue.isEmpty()) {
            return null;
        }

        if (normalizedValue.length() > maxLength) {
            throw new BizException(40000, tooLongMessage);
        }

        return normalizedValue;
    }

    private Integer validateBinaryStatus(
            Integer value,
            String message
    ) {
        if (value == null) {
            throw new BizException(40000, message);
        }

        if (value != 0 && value != 1) {
            throw new BizException(40000, message);
        }

        return value;
    }

    private void validateMenuId(Long menuId) {
        if (menuId == null || menuId < 1) {
            throw new BizException(40000, "菜单 ID 不正确");
        }
    }

    private record NormalizedMenu(
            String name,
            String path,
            String component,
            String icon,
            Integer sortOrder,
            Long parentId,
            Integer isHidden,
            Integer status
    ) {
    }
}
