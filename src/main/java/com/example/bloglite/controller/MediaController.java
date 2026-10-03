package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.MediaUploadResultDTO;
import com.example.bloglite.enums.MediaScene;
import com.example.bloglite.service.MediaStorageService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

/**
 * 后台媒体上传接口
 */
@RestController
@RequestMapping("/api/v1/admin/media")
public class MediaController {

    private final MediaStorageService mediaStorageService;

    public MediaController(MediaStorageService mediaStorageService) {
        this.mediaStorageService = mediaStorageService;
    }

    /**
     * 上传单张图片
     */
    @PostMapping(
            value = "/images",
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ApiResponse<MediaUploadResultDTO> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("scene") String scene
    ) {
        return ApiResponse.success(
                mediaStorageService.upload(
                        file,
                        MediaScene.fromCode(scene)
                )
        );
    }
}
