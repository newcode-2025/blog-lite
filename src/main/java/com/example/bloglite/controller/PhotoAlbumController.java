package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminPhotoAlbumSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.PhotoAlbumDTO;
import com.example.bloglite.service.PhotoAlbumService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 相册接口
 */
@RestController
public class PhotoAlbumController {

    private final PhotoAlbumService photoAlbumService;

    public PhotoAlbumController(
            PhotoAlbumService photoAlbumService
    ) {
        this.photoAlbumService = photoAlbumService;
    }

    /**
     * 查询全部公开相册
     */
    @GetMapping("/api/v1/albums")
    public ApiResponse<List<PhotoAlbumDTO>> listPublicAlbums() {
        return ApiResponse.success(
                photoAlbumService.listPublicAlbums()
        );
    }

    /**
     * 后台分页查询相册
     */
    @GetMapping("/api/v1/admin/albums")
    public ApiResponse<PageResultDTO<PhotoAlbumDTO>>
    listAdminAlbums(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                photoAlbumService.listAdminAlbums(
                        status,
                        keywords,
                        current,
                        size
                )
        );
    }

    /**
     * 新建相册
     */
    @PostMapping("/api/v1/admin/albums")
    public ApiResponse<PhotoAlbumDTO> createAlbum(
            @Valid @RequestBody AdminPhotoAlbumSaveRequest request
    ) {
        return ApiResponse.success(
                photoAlbumService.createAlbum(request)
        );
    }

    /**
     * 编辑相册
     */
    @PutMapping("/api/v1/admin/albums/{albumId}")
    public ApiResponse<PhotoAlbumDTO> updateAlbum(
            @PathVariable Long albumId,
            @Valid @RequestBody AdminPhotoAlbumSaveRequest request
    ) {
        return ApiResponse.success(
                photoAlbumService.updateAlbum(albumId, request)
        );
    }

    /**
     * 软删除相册及其图片
     */
    @DeleteMapping("/api/v1/admin/albums/{albumId}")
    public ApiResponse<Void> deleteAlbum(
            @PathVariable Long albumId
    ) {
        photoAlbumService.deleteAlbum(albumId);
        return ApiResponse.success(null);
    }
}