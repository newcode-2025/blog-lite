package com.example.bloglite.controller;

import com.example.bloglite.comment.ApiResponse;
import com.example.bloglite.dto.ArticleCardDTO;
import com.example.bloglite.dto.CategoryDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.ArticleService;
import com.example.bloglite.service.CategoryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 分类接口
 */
@RestController
@RequestMapping("/api/v1/categories")
public class CategoryController {

    private final CategoryService categoryService;
    private final ArticleService articleService;

    public CategoryController(CategoryService categoryService, ArticleService articleService, ArticleService articleService1) {
        this.categoryService = categoryService;
        this.articleService = articleService1;
    }


    @GetMapping
    public ApiResponse<List<CategoryDTO>> listCategories() {
        return ApiResponse.success(categoryService.listCategories());
    }

    @GetMapping("/{categoryId}/articles")
    public ApiResponse<PageResultDTO<ArticleCardDTO>> listArticlesByCategoryId(
            @PathVariable Long categoryId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        PageResultDTO<ArticleCardDTO> page =
                articleService.listArticlesByCategoryId(
                        categoryId,
                        current,
                        size
                );

        return ApiResponse.success(page);
    }
}