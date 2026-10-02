package com.example.bloglite.service;

import com.example.bloglite.dto.AdminPhotoAlbumSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.PhotoAlbumDTO;

import java.util.List;

/**
 * 相册业务接口
 */
public interface PhotoAlbumService {

    /**
     * 查询全部公开相册
     */
    List<PhotoAlbumDTO> listPublicAlbums();

    /**
     * 后台分页查询相册
     */
    PageResultDTO<PhotoAlbumDTO> listAdminAlbums(
            Integer status,
            String keywords,
            long current,
            long size
    );

    /**
     * 新建相册
     */
    PhotoAlbumDTO createAlbum(AdminPhotoAlbumSaveRequest request);

    /**
     * 编辑相册
     */
    PhotoAlbumDTO updateAlbum(
            Long albumId,
            AdminPhotoAlbumSaveRequest request
    );

    /**
     * 软删除相册及其图片
     */
    void deleteAlbum(Long albumId);
}