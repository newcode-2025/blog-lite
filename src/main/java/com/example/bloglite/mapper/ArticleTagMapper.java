package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.ArticleTag;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章标签关联数据访问接口
 */
@Mapper
public interface ArticleTagMapper extends BaseMapper<ArticleTag> {

    /**
     * 批量保存文章标签关联
     */
    int insertBatch(
            @Param("articleId") Long articleId,
            @Param("tagIds") List<Long> tagIds
    );

    /**
     * 删除指定文章的全部标签关联
     */
    int deleteByArticleId(
            @Param("articleId") Long articleId
    );
}