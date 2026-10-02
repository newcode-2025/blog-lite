package com.example.bloglite.service;

import com.example.bloglite.dto.AdminPhotoSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.PhotoDTO;

/**
 * 相册图片业务接口
 */
public interface PhotoService {

    /**
     * 分页查询公开相册中的图片
     */
    PageResultDTO<PhotoDTO> listPublicPhotos(
            Long albumId,
            long current,
            long size
    );

    /**
     * 后台分页查询相册中的图片
     */
    PageResultDTO<PhotoDTO> listAdminPhotos(
            Long albumId,
            long current,
            long size
    );

    /**
     * 新建图片
     */
    PhotoDTO createPhoto(
            Long albumId,
            AdminPhotoSaveRequest request
    );

    /**
     * 编辑图片
     */
    PhotoDTO updatePhoto(
            Long photoId,
            AdminPhotoSaveRequest request
    );

    /**
     * 软删除图片
     */
    void deletePhoto(Long photoId);
}