package com.example.bloglite.service;

import com.example.bloglite.dto.MediaUploadResultDTO;
import com.example.bloglite.enums.MediaScene;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 媒体上传业务接口
 */
public interface MediaStorageService {

    /**
     * 上传单张图片
     */
    MediaUploadResultDTO upload(
            MultipartFile file,
            MediaScene scene
    );

    /**
     * 批量上传图片
     */
    List<MediaUploadResultDTO> uploadBatch(
            List<MultipartFile> files,
            MediaScene scene
    );
}
