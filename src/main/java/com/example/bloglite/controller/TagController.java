package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.ArticleCardDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.TagListItemDTO;
import com.example.bloglite.service.ArticleService;
import com.example.bloglite.service.TagService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 标签接口
 */
@RestController
@RequestMapping("/api/v1/tags")
public class TagController {

    private final TagService tagService;
    private final ArticleService articleService;

    public TagController(TagService tagService, ArticleService articleService) {
        this.tagService = tagService;
        this.articleService = articleService;
    }

    @GetMapping
    public ApiResponse<List<TagListItemDTO>> listTags() {
        return ApiResponse.success(tagService.listTags());
    }

    @GetMapping("/{tagId}/articles")
    public ApiResponse<PageResultDTO<ArticleCardDTO>> listArticlesByTagId(
            @PathVariable Long tagId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        PageResultDTO<ArticleCardDTO> page =
                articleService.listArticlesByTagId(
                        tagId,
                        current,
                        size
                );

        return ApiResponse.success(page);
    }
}