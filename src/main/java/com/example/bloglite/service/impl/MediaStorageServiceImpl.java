package com.example.bloglite.service.impl;

import com.example.bloglite.config.UploadProperties;
import com.example.bloglite.dto.MediaUploadResultDTO;
import com.example.bloglite.dto.ValidatedImage;
import com.example.bloglite.enums.MediaScene;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.service.ImageFileValidator;
import com.example.bloglite.service.MediaFileStorage;
import com.example.bloglite.service.MediaStorageService;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 媒体上传业务实现
 */
@Service
public class MediaStorageServiceImpl implements MediaStorageService {

    private final ImageFileValidator imageFileValidator;

    private final MediaFileStorage mediaFileStorage;

    private final UploadProperties uploadProperties;

    public MediaStorageServiceImpl(
            ImageFileValidator imageFileValidator,
            MediaFileStorage mediaFileStorage,
            UploadProperties uploadProperties
    ) {
        this.imageFileValidator = imageFileValidator;
        this.mediaFileStorage = mediaFileStorage;
        this.uploadProperties = uploadProperties;
    }

    @Override
    public MediaUploadResultDTO upload(
            MultipartFile file,
            MediaScene scene
    ) {
        validateScene(scene);
        ValidatedImage validatedImage = imageFileValidator.validate(file);
        StoredMedia storedMedia = storeFile(file, scene, validatedImage);
        return toResult(storedMedia, scene);
    }

    @Override
    public List<MediaUploadResultDTO> uploadBatch(
            List<MultipartFile> files,
            MediaScene scene
    ) {
        validateScene(scene);

        if (files == null || files.isEmpty()) {
            throw new BizException(40000, "批量上传图片不能为空");
        }

        if (files.size() > uploadProperties.getMaxBatchCount()) {
            throw new BizException(
                    40000,
                    "单次最多上传 "
                            + uploadProperties.getMaxBatchCount()
                            + " 张图片"
            );
        }

        List<ValidatedImage> validatedImages = files.stream()
                .map(imageFileValidator::validate)
                .toList();

        List<StoredMedia> storedMediaList = new ArrayList<>();
        List<MediaUploadResultDTO> results = new ArrayList<>();

        try {
            for (int index = 0; index < files.size(); index++) {
                StoredMedia storedMedia = storeFile(
                        files.get(index),
                        scene,
                        validatedImages.get(index)
                );
                storedMediaList.add(storedMedia);
                results.add(toResult(storedMedia, scene));
            }

            return results;
        } catch (RuntimeException exception) {
            rollbackStoredFiles(storedMediaList);
            throw exception;
        }
    }

    private StoredMedia storeFile(
            MultipartFile file,
            MediaScene scene,
            ValidatedImage validatedImage
    ) {
        try {
            String relativePath = mediaFileStorage.store(
                    file.getInputStream(),
                    scene,
                    validatedImage.getFormat().getStoredExtension()
            );

            return new StoredMedia(relativePath, validatedImage);
        } catch (IOException exception) {
            throw new BizException(
                    50000,
                    "读取上传图片失败，请稍后重试",
                    exception
            );
        }
    }

    private MediaUploadResultDTO toResult(
            StoredMedia storedMedia,
            MediaScene scene
    ) {
        ValidatedImage image = storedMedia.validatedImage();

        return new MediaUploadResultDTO(
                buildPublicUrl(storedMedia.relativePath()),
                image.getOriginalName(),
                image.getFormat().getContentType(),
                image.getSize(),
                image.getWidth(),
                image.getHeight(),
                scene.getCode()
        );
    }

    private String buildPublicUrl(String relativePath) {
        return normalizePublicPath() + "/" + relativePath;
    }

    private String normalizePublicPath() {
        String publicPath = uploadProperties.getPublicPath()
                .trim()
                .replace('\\', '/');

        if (!publicPath.startsWith("/")) {
            publicPath = "/" + publicPath;
        }

        while (publicPath.endsWith("/") && publicPath.length() > 1) {
            publicPath = publicPath.substring(0, publicPath.length() - 1);
        }

        return publicPath;
    }

    private void validateScene(MediaScene scene) {
        if (scene == null) {
            throw new BizException(40000, "上传场景不能为空");
        }
    }

    private void rollbackStoredFiles(List<StoredMedia> storedMediaList) {
        for (StoredMedia storedMedia : storedMediaList) {
            try {
                mediaFileStorage.delete(storedMedia.relativePath());
            } catch (RuntimeException ignored) {
                // 批量失败时尽力清理已经写入的文件
            }
        }
    }

    /**
     * 已保存媒体文件
     */
    private record StoredMedia(
            String relativePath,
            ValidatedImage validatedImage
    ) {
    }

    @Override
    public void deleteByUrl(String url) {
        String relativePath = toRelativePath(url);

        if (mediaFileStorage.resolve(relativePath).isEmpty()) {
            throw new BizException(40000, "媒体文件地址不合法");
        }

        mediaFileStorage.delete(relativePath);
    }

    private String toRelativePath(String url) {
        if (url == null || url.trim().isEmpty()) {
            throw new BizException(40000, "媒体文件地址不能为空");
        }

        String normalizedUrl = url.trim().replace('\\', '/');
        String publicPath = normalizePublicPath();

        if (!normalizedUrl.startsWith(publicPath + "/")) {
            throw new BizException(40000, "媒体文件地址不属于本地上传目录");
        }

        String relativePath = normalizedUrl.substring(
                publicPath.length() + 1
        );

        if (relativePath.isEmpty()) {
            throw new BizException(40000, "媒体文件地址不合法");
        }

        return relativePath;
    }
}
