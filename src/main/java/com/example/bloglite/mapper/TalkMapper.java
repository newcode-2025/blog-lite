package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.TalkDTO;
import com.example.bloglite.entity.Talk;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 说说数据访问接口
 */
@Mapper
public interface TalkMapper extends BaseMapper<Talk> {

    /**
     * 分页查询公开说说
     */
    List<TalkDTO> selectPublicTalks(
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计公开说说数量
     */
    long countPublicTalks();

    /**
     * 后台分页查询说说
     */
    List<TalkDTO> selectAdminTalks(
            @Param("status") Integer status,
            @Param("keywords") String keywords,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计后台说说数量
     */
    long countAdminTalks(
            @Param("status") Integer status,
            @Param("keywords") String keywords
    );

    /**
     * 根据 ID 查询后台说说数据
     */
    TalkDTO selectAdminTalkById(
            @Param("talkId") Long talkId
    );
}