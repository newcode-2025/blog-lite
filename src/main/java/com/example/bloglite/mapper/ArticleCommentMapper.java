package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.ArticleCommentListDTO;
import com.example.bloglite.dto.ArticleCommentReviewDTO;
import com.example.bloglite.dto.ArticleReplyDTO;
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

    /**
     * 分页查询某个顶级评论下审核通过的回复
     */
    List<ArticleReplyDTO> selectApprovedReplies(
            @Param("parentId") Long parentId,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 查询某个顶级评论下审核通过的回复总数
     */
    long countApprovedReplies(
            @Param("parentId") Long parentId
    );

    /**
     * 文章作者分页查询评论审核列表
     */
    List<ArticleCommentReviewDTO> selectReviewComments(
            @Param("articleId") Long articleId,
            @Param("status") Integer status,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 文章作者查询评论审核列表总数
     */
    long countReviewComments(
            @Param("articleId") Long articleId,
            @Param("status") Integer status
    );
}