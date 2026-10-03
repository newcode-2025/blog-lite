package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.AdminUserRoleDTO;
import com.example.bloglite.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台用户数据访问接口
 */
@Mapper
public interface AdminUserMapper extends BaseMapper<UserAccount> {

    /**
     * 分页查询后台用户
     */
    List<UserAccount> selectAdminUsers(
            @Param("keywords") String keywords,
            @Param("status") Integer status,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计后台用户数量
     */
    long countAdminUsers(
            @Param("keywords") String keywords,
            @Param("status") Integer status
    );

    /**
     * 查询多个用户的角色
     */
    List<AdminUserRoleDTO> selectRolesByUserIds(
            @Param("userIds") List<Long> userIds
    );

    /**
     * 统计指定角色下其他启用用户的数量
     */
    long countEnabledUsersByRoleCode(
            @Param("roleCode") String roleCode,
            @Param("excludeUserId") Long excludeUserId
    );
}
