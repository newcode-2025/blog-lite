package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.FriendLinkDTO;
import com.example.bloglite.entity.FriendLink;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 友情链接数据访问接口
 */
@Mapper
public interface FriendLinkMapper extends BaseMapper<FriendLink> {

    /**
     * 查询全部显示状态的友链
     */
    List<FriendLinkDTO> selectPublicLinks();

    /**
     * 后台分页查询友链
     */
    List<FriendLinkDTO> selectAdminLinks(
            @Param("status") Integer status,
            @Param("keywords") String keywords,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计后台友链数量
     */
    long countAdminLinks(
            @Param("status") Integer status,
            @Param("keywords") String keywords
    );

    /**
     * 根据 ID 查询友链
     */
    FriendLinkDTO selectAdminLinkById(
            @Param("linkId") Long linkId
    );

    /**
     * 统计相同地址的友链，编辑时排除当前友链
     */
    long countByAddress(
            @Param("address") String address,
            @Param("excludeId") Long excludeId
    );
}
