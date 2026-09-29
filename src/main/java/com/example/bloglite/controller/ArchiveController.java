package com.example.bloglite.controller;

import com.example.bloglite.comment.ApiResponse;
import com.example.bloglite.dto.ArchiveDTO;
import com.example.bloglite.service.ArticleService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 文章归档接口
 */
@RestController
@RequestMapping("/api/v1/archives")
public class ArchiveController {

    private final ArticleService articleService;

    public ArchiveController(ArticleService articleService) {
        this.articleService = articleService;
    }


    @GetMapping
    public ApiResponse<List<ArchiveDTO>> listArchives() {
        return ApiResponse.success(articleService.listArchives());
    }
}
