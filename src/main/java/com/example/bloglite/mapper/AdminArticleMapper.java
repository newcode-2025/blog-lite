package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.AdminArticleDetailDTO;
import com.example.bloglite.dto.AdminArticleListItemDTO;
import com.example.bloglite.entity.Article;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台文章数据访问接口
 */
@Mapper
public interface AdminArticleMapper extends BaseMapper<Article> {

    /**
     * 分页查询当前用户的文章
     */
    List<AdminArticleListItemDTO> selectAdminArticles(
            @Param("authorId") Long authorId,
            @Param("status") Integer status,
            @Param("keywords") String keywords,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计当前用户符合条件的文章数量
     */
    long countAdminArticles(
            @Param("authorId") Long authorId,
            @Param("status") Integer status,
            @Param("keywords") String keywords
    );

    /**
     * 查询后台文章编辑详情
     */
    AdminArticleDetailDTO selectAdminArticleDetail(
            @Param("articleId") Long articleId
    );
}