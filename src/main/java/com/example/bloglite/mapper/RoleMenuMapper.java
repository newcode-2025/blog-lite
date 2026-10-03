package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.RoleMenu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色菜单关联数据访问接口
 */
@Mapper
public interface RoleMenuMapper extends BaseMapper<RoleMenu> {
}
