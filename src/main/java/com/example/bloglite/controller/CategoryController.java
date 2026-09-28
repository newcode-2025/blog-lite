package com.example.bloglite.controller;

import com.example.bloglite.comment.ApiResponse;
import com.example.bloglite.dto.CategoryDTO;
import com.example.bloglite.service.CategoryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 分类接口
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }


    @GetMapping
    public ApiResponse<List<CategoryDTO>> listCategories() {
        return ApiResponse.success(categoryService.listCategories());
    }
}