package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminPhotoSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.PhotoDTO;
import com.example.bloglite.service.PhotoService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 相册图片接口
 */
@RestController
public class PhotoController {

    private final PhotoService photoService;

    public PhotoController(PhotoService photoService) {
        this.photoService = photoService;
    }

    /**
     * 分页查询公开相册中的图片
     */
    @GetMapping("/api/v1/albums/{albumId}/photos")
    public ApiResponse<PageResultDTO<PhotoDTO>> listPublicPhotos(
            @PathVariable Long albumId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                photoService.listPublicPhotos(
                        albumId,
                        current,
                        size
                )
        );
    }

    /**
     * 后台分页查询相册中的图片
     */
    @GetMapping("/api/v1/admin/albums/{albumId}/photos")
    public ApiResponse<PageResultDTO<PhotoDTO>> listAdminPhotos(
            @PathVariable Long albumId,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                photoService.listAdminPhotos(
                        albumId,
                        current,
                        size
                )
        );
    }

    /**
     * 新建图片
     */
    @PostMapping("/api/v1/admin/albums/{albumId}/photos")
    public ApiResponse<PhotoDTO> createPhoto(
            @PathVariable Long albumId,
            @Valid @RequestBody AdminPhotoSaveRequest request
    ) {
        return ApiResponse.success(
                photoService.createPhoto(albumId, request)
        );
    }

    /**
     * 编辑图片
     */
    @PutMapping("/api/v1/admin/photos/{photoId}")
    public ApiResponse<PhotoDTO> updatePhoto(
            @PathVariable Long photoId,
            @Valid @RequestBody AdminPhotoSaveRequest request
    ) {
        return ApiResponse.success(
                photoService.updatePhoto(photoId, request)
        );
    }

    /**
     * 软删除图片
     */
    @DeleteMapping("/api/v1/admin/photos/{photoId}")
    public ApiResponse<Void> deletePhoto(
            @PathVariable Long photoId
    ) {
        photoService.deletePhoto(photoId);
        return ApiResponse.success(null);
    }
}