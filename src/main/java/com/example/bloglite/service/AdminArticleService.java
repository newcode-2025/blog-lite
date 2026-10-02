package com.example.bloglite.service;

import com.example.bloglite.dto.AdminArticleCreateRequest;
import com.example.bloglite.dto.AdminArticleCreateResultDTO;
import com.example.bloglite.dto.AdminArticleListItemDTO;
import com.example.bloglite.dto.PageResultDTO;

/**
 * 后台文章业务接口
 */
public interface AdminArticleService {

    /**
     * 分页查询当前登录用户的文章
     */
    PageResultDTO<AdminArticleListItemDTO> listArticles(
            Integer status,
            String keywords,
            long current,
            long size
    );

    /**
     * 新建当前登录用户的文章
     */
    AdminArticleCreateResultDTO createArticle(
            AdminArticleCreateRequest request
    );
}