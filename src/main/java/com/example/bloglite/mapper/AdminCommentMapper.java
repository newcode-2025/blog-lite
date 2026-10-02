package com.example.bloglite.mapper;

import com.example.bloglite.dto.AdminCommentDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 后台评论数据访问接口
 */
@Mapper
public interface AdminCommentMapper {

    /**
     * 分页查询当前用户文章下的评论
     */
    List<AdminCommentDTO> selectAdminComments(
            @Param("authorId") Long authorId,
            @Param("articleId") Long articleId,
            @Param("status") Integer status,
            @Param("keywords") String keywords,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计当前用户文章下的评论数量
     */
    long countAdminComments(
            @Param("authorId") Long authorId,
            @Param("articleId") Long articleId,
            @Param("status") Integer status,
            @Param("keywords") String keywords
    );
}