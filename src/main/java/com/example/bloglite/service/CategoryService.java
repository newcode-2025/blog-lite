package com.example.bloglite.service;

import com.example.bloglite.dto.CategoryDTO;

import java.util.List;

/**
 * 分类业务接口
 */
public interface CategoryService {


    List<CategoryDTO> listCategories();
}
