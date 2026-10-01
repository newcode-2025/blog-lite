package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.ArticleComment;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文章评论数据访问接口
 */
@Mapper
public interface ArticleCommentMapper extends BaseMapper<ArticleComment> {
}