package com.example.bloglite.service;

import com.example.bloglite.dto.ValidatedImage;
import org.springframework.web.multipart.MultipartFile;

/**
 * 图片上传校验接口
 */
public interface ImageFileValidator {

    /**
     * 校验上传图片并返回可信的图片信息
     */
    ValidatedImage validate(MultipartFile file);
}
