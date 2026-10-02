package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.AdminCategoryDTO;
import com.example.bloglite.entity.Category;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台分类数据访问接口
 */
@Mapper
public interface AdminCategoryMapper extends BaseMapper<Category> {

    /**
     * 查询全部分类及关联文章数量
     */
    List<AdminCategoryDTO> selectAdminCategories();

    /**
     * 根据 ID 查询分类管理数据
     */
    AdminCategoryDTO selectAdminCategoryById(
            @Param("categoryId") Long categoryId
    );

    /**
     * 统计同名分类，编辑时排除当前分类
     */
    long countByCategoryName(
            @Param("name") String name,
            @Param("excludeId") Long excludeId
    );

    /**
     * 统计分类被文章引用的数量
     */
    long countArticlesByCategoryId(
            @Param("categoryId") Long categoryId
    );
}
