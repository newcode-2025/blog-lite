package com.example.bloglite.service.impl;

import com.example.bloglite.dto.CategoryDTO;
import com.example.bloglite.mapper.CategoryMapper;
import com.example.bloglite.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 分类业务实现
 */
@Service
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;

    public CategoryServiceImpl(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    @Override
    public List<CategoryDTO> listCategories() {
        return categoryMapper.selectCategories();
    }
}