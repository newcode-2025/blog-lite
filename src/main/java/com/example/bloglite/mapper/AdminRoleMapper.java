package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.AdminRoleOptionDTO;
import com.example.bloglite.entity.Role;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台角色数据访问接口
 */
@Mapper
public interface AdminRoleMapper extends BaseMapper<Role> {

    /**
     * 分页查询后台角色
     */
    List<Role> selectAdminRoles(
            @Param("keywords") String keywords,
            @Param("status") Integer status,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计后台角色数量
     */
    long countAdminRoles(
            @Param("keywords") String keywords,
            @Param("status") Integer status
    );

    /**
     * 查询启用角色选项
     */
    List<AdminRoleOptionDTO> selectEnabledRoleOptions();
}
