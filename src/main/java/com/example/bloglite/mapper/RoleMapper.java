package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.Role;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色数据访问接口
 */
@Mapper
public interface RoleMapper extends BaseMapper<Role> {
}
