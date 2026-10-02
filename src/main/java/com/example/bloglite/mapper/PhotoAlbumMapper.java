package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.PhotoAlbumDTO;
import com.example.bloglite.entity.PhotoAlbum;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 相册数据访问接口
 */
@Mapper
public interface PhotoAlbumMapper extends BaseMapper<PhotoAlbum> {

    /**
     * 查询全部公开相册
     */
    List<PhotoAlbumDTO> selectPublicAlbums();

    /**
     * 后台分页查询相册
     */
    List<PhotoAlbumDTO> selectAdminAlbums(
            @Param("status") Integer status,
            @Param("keywords") String keywords,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计后台相册数量
     */
    long countAdminAlbums(
            @Param("status") Integer status,
            @Param("keywords") String keywords
    );

    /**
     * 根据 ID 查询相册
     */
    PhotoAlbumDTO selectAdminAlbumById(
            @Param("albumId") Long albumId
    );

    /**
     * 统计同名相册，编辑时排除当前相册
     */
    long countByName(
            @Param("name") String name,
            @Param("excludeId") Long excludeId
    );

    /**
     * 软删除相册下的全部图片
     */
    int softDeletePhotosByAlbumId(
            @Param("albumId") Long albumId
    );
}