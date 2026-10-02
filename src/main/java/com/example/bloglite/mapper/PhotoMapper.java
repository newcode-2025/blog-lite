package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.PhotoDTO;
import com.example.bloglite.entity.Photo;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 相册图片数据访问接口
 */
@Mapper
public interface PhotoMapper extends BaseMapper<Photo> {

    /**
     * 分页查询公开相册中的图片
     */
    List<PhotoDTO> selectPublicPhotos(
            @Param("albumId") Long albumId,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计公开相册中的图片数量
     */
    long countPublicPhotos(
            @Param("albumId") Long albumId
    );

    /**
     * 后台分页查询相册中的图片
     */
    List<PhotoDTO> selectAdminPhotos(
            @Param("albumId") Long albumId,
            @Param("offset") long offset,
            @Param("size") long size
    );

    /**
     * 统计后台相册中的图片数量
     */
    long countAdminPhotos(
            @Param("albumId") Long albumId
    );

    /**
     * 根据 ID 查询后台图片数据
     */
    PhotoDTO selectAdminPhotoById(
            @Param("photoId") Long photoId
    );
}