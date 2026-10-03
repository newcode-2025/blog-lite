package com.example.bloglite.service;

import com.example.bloglite.dto.AdminMenuDTO;
import com.example.bloglite.dto.AdminMenuSaveRequest;

import java.util.List;

/**
 * 后台菜单业务接口
 */
public interface AdminMenuService {

    /**
     * 查询完整菜单树
     */
    List<AdminMenuDTO> listMenuTree();

    /**
     * 查询菜单详情
     */
    AdminMenuDTO getMenuDetail(Long menuId);

    /**
     * 新增菜单
     */
    AdminMenuDTO createMenu(AdminMenuSaveRequest request);

    /**
     * 编辑菜单
     */
    AdminMenuDTO updateMenu(
            Long menuId,
            AdminMenuSaveRequest request
    );

    /**
     * 删除未被使用的菜单
     */
    void deleteMenu(Long menuId);
}
