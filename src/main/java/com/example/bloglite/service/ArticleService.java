package com.example.bloglite.service;

import com.example.bloglite.dto.ArticleCardDTO;
import com.example.bloglite.dto.ArticleDetailDTO;
import com.example.bloglite.dto.PageResultDTO;


public interface ArticleService {

    PageResultDTO<ArticleCardDTO> listArticles(long current, long size);

    ArticleDetailDTO getArticleById(Long id);
}
