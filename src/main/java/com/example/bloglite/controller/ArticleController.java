package com.example.bloglite.controller;

import com.example.bloglite.comment.ApiResponse;
import com.example.bloglite.dto.ArticleCardDTO;
import com.example.bloglite.dto.ArticleDetailDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.ArticleService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/articles")
public class ArticleController {
    private final ArticleService articleService;

    public ArticleController(ArticleService articleService) {
        this.articleService = articleService;
    }

    @GetMapping
    public ApiResponse<PageResultDTO<ArticleCardDTO>> listArticles(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        PageResultDTO<ArticleCardDTO> page =
                articleService.listArticles(current, size);

        return ApiResponse.success(page);
    }

    @GetMapping("/{id}")
    public ApiResponse<ArticleDetailDTO> getArticleById(
            @PathVariable Long id
    ) {
        ArticleDetailDTO article = articleService.getArticleById(id);

        if (article == null) {
            return ApiResponse.error("文章不存在");
        }

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
