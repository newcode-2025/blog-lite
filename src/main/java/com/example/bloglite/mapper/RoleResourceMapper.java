package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.RoleResource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 角色接口资源关联数据访问接口
 */
@Mapper
public interface RoleResourceMapper extends BaseMapper<RoleResource> {
}
