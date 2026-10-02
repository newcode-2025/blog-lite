package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.AdminTagDTO;
import com.example.bloglite.entity.Tag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台标签数据访问接口
 */
@Mapper
public interface AdminTagMapper extends BaseMapper<Tag> {

    /**
     * 查询全部标签及关联文章数量
     */
    List<AdminTagDTO> selectAdminTags();

    /**
     * 根据 ID 查询标签管理数据
     */
    AdminTagDTO selectAdminTagById(
            @Param("tagId") Long tagId
    );

    /**
     * 统计同名标签，编辑时排除当前标签
     */
    long countByTagName(
            @Param("name") String name,
            @Param("excludeId") Long excludeId
    );

    /**
     * 统计标签被文章引用的数量
     */
    long countArticleTagsByTagId(
            @Param("tagId") Long tagId
    );
}