package com.example.bloglite.controller;

import com.example.bloglite.comment.ApiResponse;
import com.example.bloglite.dto.ArticleCardDTO;
import com.example.bloglite.dto.ArticleDetailDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 文章阅读接口
 */
@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {

    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    /**
     * 分页查询公开文章
     */
    @GetMapping
    public ApiResponse<PageResultDTO<ArticleCardDTO>> listArticles(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        PageResultDTO<ArticleCardDTO> page =
                articleService.listArticles(current, size);

        return ApiResponse.success(page);
    }

    /**
     * 查询文章详情
     */
    @GetMapping("/{id}")
    public ApiResponse<ArticleDetailDTO> getArticleById(
            @PathVariable Long id
    ) {
        ArticleDetailDTO article = articleService.getArticleById(id);
        return ApiResponse.success(article);
    }

    /**
     * 搜索公开文章
     */
    @GetMapping("/search")
    public ApiResponse<PageResultDTO<ArticleCardDTO>> searchArticles(
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        PageResultDTO<ArticleCardDTO> page =
                articleService.searchArticles(
                        keywords,
                        current,
                        size
                );

        return ApiResponse.success(page);
    }
}