package com.example.bloglite.service;

import com.example.bloglite.dto.ArchiveDTO;
import com.example.bloglite.dto.ArticleCardDTO;
import com.example.bloglite.dto.ArticleDetailDTO;
import com.example.bloglite.dto.PageResultDTO;

import java.util.List;


public interface ArticleService {

    PageResultDTO<ArticleCardDTO> listArticles(long current, long size);

    ArticleDetailDTO getArticleById(Long id);

    PageResultDTO<ArticleCardDTO> listArticlesByCategoryId(
            Long categoryId,
            long current,
            long size
    );

    PageResultDTO<ArticleCardDTO> listArticlesByTagId(
            Long tagId,
            long current,
            long size
    );

    List<ArchiveDTO> listArchives();

    PageResultDTO<ArticleCardDTO> searchArticles(
            String keywords,
            long current,
            long size
    );
}
