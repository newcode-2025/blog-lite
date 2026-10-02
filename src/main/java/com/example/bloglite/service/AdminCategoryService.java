package com.example.bloglite.service;

import com.example.bloglite.dto.AdminCategoryCreateRequest;
import com.example.bloglite.dto.AdminCategoryDTO;
import com.example.bloglite.dto.AdminCategoryUpdateRequest;

import java.util.List;

/**
 * 后台分类业务接口
 */
public interface AdminCategoryService {

    /**
     * 查询全部分类
     */
    List<AdminCategoryDTO> listCategories();

    /**
     * 新建分类
     */
    AdminCategoryDTO createCategory(
            AdminCategoryCreateRequest request
    );

    /**
     * 编辑分类
     */
    AdminCategoryDTO updateCategory(
            Long categoryId,
            AdminCategoryUpdateRequest request
    );

    /**
     * 删除未被文章使用的分类
     */
    void deleteCategory(Long categoryId);
}