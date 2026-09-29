package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.ArticleCardDTO;
import com.example.bloglite.dto.ArticleDetailDTO;
import com.example.bloglite.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ArticleMapper extends BaseMapper<Article> {

    List<ArticleCardDTO> selectArticleCards( @Param("offset") long offset,
                                             @Param("size") long size);

    long countPublishedArticles();

    ArticleDetailDTO selectArticleDetailById(@Param("id") Long id);

    int incrementViewCount(@Param("id") Long id);


    List<ArticleCardDTO> selectArticleCardsByCategoryId(
            @Param("categoryId") Long categoryId,
            @Param("offset") long offset,
            @Param("size") long size
    );

    long countPublishedArticlesByCategoryId(
            @Param("categoryId") Long categoryId
    );

    List<ArticleCardDTO> selectArticleCardsByTagId(
            @Param("tagId") Long tagId,
            @Param("offset") long offset,
            @Param("size") long size
    );

    long countPublishedArticlesByTagId(@Param("tagId") Long tagId);

    List<ArticleCardDTO> selectArchiveArticles();
}
