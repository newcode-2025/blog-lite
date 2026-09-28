package com.example.bloglite.service.impl;

import com.example.bloglite.dto.ArticleCardDTO;
import com.example.bloglite.dto.ArticleDetailDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.mapper.ArticleMapper;
import com.example.bloglite.service.ArticleService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ArticleServiceImpl implements ArticleService {
    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private final ArticleMapper articleMapper;

    public ArticleServiceImpl(ArticleMapper articleMapper){
        this.articleMapper=articleMapper;
    }

    @Override
    public PageResultDTO<ArticleCardDTO> listArticles(long current,long size) {
        if (current < 1) {
            current = 1;
        }

        if (size < 1) {
            size = DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        long offset = (current - 1) * size;

        List<ArticleCardDTO> records =
                articleMapper.selectArticleCards(offset, size);

        long count = articleMapper.countPublishedArticles();
        return new PageResultDTO<>(records, count, current, size);
    }

    @Transactional
    @Override
    public ArticleDetailDTO getArticleById(Long id) {
        if (id == null || id < 1) {
            return null;
        }

        ArticleDetailDTO article =
                articleMapper.selectArticleDetailById(id);

        if (article == null) {
            return null;
        }

        int rows = articleMapper.incrementViewCount(id);

        if (rows > 0) {
            long currentViewCount =
                    article.getViewCount() == null ? 0 : article.getViewCount();

            article.setViewCount(currentViewCount + 1);
        }

        return article;
    }

    @Override
    public PageResultDTO<ArticleCardDTO> listArticlesByCategoryId(
            Long categoryId,
            long current,
            long size
    ) {
        if (current < 1) {
            current = 1;
        }

        if (size < 1) {
            size = DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        if (categoryId == null || categoryId < 1) {
            return new PageResultDTO<>(
                    List.of(),
                    0L,
                    current,
                    size
            );
        }

        long offset = (current - 1) * size;

        List<ArticleCardDTO> records =
                articleMapper.selectArticleCardsByCategoryId(
                        categoryId,
                        offset,
                        size
                );

        long count =
                articleMapper.countPublishedArticlesByCategoryId(categoryId);

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    @Override
    public PageResultDTO<ArticleCardDTO> listArticlesByTagId(
            Long tagId,
            long current,
            long size
    ) {
        if (current < 1) {
            current = 1;
        }

        if (size < 1) {
            size = DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            size = MAX_SIZE;
        }

        if (tagId == null || tagId < 1) {
            return new PageResultDTO<>(
                    List.of(),
                    0L,
                    current,
                    size
            );
        }

        long offset = (current - 1) * size;

        List<ArticleCardDTO> records =
                articleMapper.selectArticleCardsByTagId(
                        tagId,
                        offset,
                        size
                );

        long count =
                articleMapper.countPublishedArticlesByTagId(tagId);

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }
}
