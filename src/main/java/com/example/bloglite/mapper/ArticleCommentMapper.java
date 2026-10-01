package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.ArticleCommentListDTO;
import com.example.bloglite.entity.ArticleComment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 文章评论数据访问接口
 */
@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {

    /**
     * 分页查询某篇文章下审核通过的顶级评论
     */
    List<ArticleCommentListDTO> selectApprovedTopLevelComments(
            @Param("articleId") Long articleId,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 查询某篇文章下审核通过的顶级评论总数
     */
    long countApprovedTopLevelComments(
            @Param("articleId") Long articleId
    );
}