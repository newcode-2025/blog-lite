package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.AdminPhotoSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.PhotoDTO;
import com.example.bloglite.entity.Photo;
import com.example.bloglite.entity.PhotoAlbum;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.PhotoAlbumMapper;
import com.example.bloglite.mapper.PhotoMapper;
import com.example.bloglite.service.PhotoService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 相册图片业务实现
 */
@Service
public class PhotoServiceImpl implements PhotoService {

    private static final int ALBUM_STATUS_PUBLIC = 1;

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private static final int DEFAULT_SORT_ORDER = 0;

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private final PhotoMapper photoMapper;
    private final PhotoAlbumMapper photoAlbumMapper;

    public PhotoServiceImpl(
            PhotoMapper photoMapper,
            PhotoAlbumMapper photoAlbumMapper
    ) {
        this.photoMapper = photoMapper;
        this.photoAlbumMapper = photoAlbumMapper;
    }

    /**
     * 分页查询公开相册中的图片
     */
    @Override
    public PageResultDTO<PhotoDTO> listPublicPhotos(
            Long albumId,
            long current,
            long size
    ) {
        PhotoAlbum album = getAlbumOrThrow(albumId);

        if (!Integer.valueOf(ALBUM_STATUS_PUBLIC)
                .equals(album.getStatus())) {
            throw new BizException(40400, "相册不存在");
        }

        current = normalizeCurrent(current);
        size = normalizeSize(size);

        long offset = (current - 1) * size;

        List<PhotoDTO> records = photoMapper.selectPublicPhotos(
                albumId,
                offset,
                size
        );

        long count = photoMapper.countPublicPhotos(albumId);

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    /**
     * 后台分页查询相册中的图片
     */
    @Override
    public PageResultDTO<PhotoDTO> listAdminPhotos(
            Long albumId,
            long current,
            long size
    ) {
        getAlbumOrThrow(albumId);

        current = normalizeCurrent(current);
        size = normalizeSize(size);

        long offset = (current - 1) * size;

        List<PhotoDTO> records = photoMapper.selectAdminPhotos(
                albumId,
                offset,
                size
        );

        long count = photoMapper.countAdminPhotos(albumId);

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    /**
     * 新建图片
     */
    @Transactional
    @Override
    public PhotoDTO createPhoto(
            Long albumId,
            AdminPhotoSaveRequest request
    ) {
        getAlbumOrThrow(albumId);

        String photoUrl = normalizeRequiredText(
                request.getPhotoUrl(),
                "图片地址不能为空"
        );

        int sortOrder = request.getSortOrder() == null
                ? DEFAULT_SORT_ORDER
                : request.getSortOrder();

        LocalDateTime now = LocalDateTime.now();

        Photo photo = new Photo();
        photo.setAlbumId(albumId);
        photo.setName(normalizeOptionalText(request.getName()));
        photo.setDescription(
                normalizeOptionalText(request.getDescription())
        );
        photo.setPhotoUrl(photoUrl);
        photo.setSortOrder(sortOrder);
        photo.setIsDeleted(NOT_DELETED);
        photo.setCreatedAt(now);
        photo.setUpdatedAt(now);

        int insertedRows = photoMapper.insert(photo);

        if (insertedRows != 1 || photo.getId() == null) {
            throw new BizException(
                    50000,
                    "图片创建失败，请稍后重试"
            );
        }

        return loadPhoto(photo.getId());
    }

    /**
     * 编辑图片
     */
    @Transactional
    @Override
    public PhotoDTO updatePhoto(
            Long photoId,
            AdminPhotoSaveRequest request
    ) {
        Photo existingPhoto = getPhotoOrThrow(photoId);

        getAlbumOrThrow(existingPhoto.getAlbumId());

        String photoUrl = normalizeRequiredText(
                request.getPhotoUrl(),
                "图片地址不能为空"
        );

        int sortOrder = request.getSortOrder() == null
                ? existingPhoto.getSortOrder()
                : request.getSortOrder();

        Photo updatePhoto = new Photo();
        updatePhoto.setId(photoId);
        updatePhoto.setName(normalizeOptionalText(request.getName()));
        updatePhoto.setDescription(
                normalizeOptionalText(request.getDescription())
        );
        updatePhoto.setPhotoUrl(photoUrl);
        updatePhoto.setSortOrder(sortOrder);
        updatePhoto.setUpdatedAt(LocalDateTime.now());

        int updatedRows = photoMapper.updateById(updatePhoto);

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "图片更新失败，请稍后重试"
            );
        }

        return loadPhoto(photoId);
    }

    /**
     * 软删除图片
     */
    @Transactional
    @Override
    public void deletePhoto(Long photoId) {
        Photo photo = getPhotoOrThrow(photoId);

        getAlbumOrThrow(photo.getAlbumId());

        LambdaUpdateWrapper<Photo> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Photo::getId, photoId)
                .eq(Photo::getIsDeleted, NOT_DELETED)
                .set(Photo::getIsDeleted, DELETED)
                .set(Photo::getUpdatedAt, LocalDateTime.now());

        int updatedRows = photoMapper.update(null, updateWrapper);

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "图片删除失败，请稍后重试"
            );
        }
    }

    /**
     * 查询未删除且所属相册有效的图片
     */
    private Photo getPhotoOrThrow(Long photoId) {
        if (photoId == null || photoId < 1) {
            throw new BizException(40000, "图片 ID 不正确");
        }

        Photo photo = photoMapper.selectById(photoId);

        if (photo == null
                || Integer.valueOf(DELETED)
                .equals(photo.getIsDeleted())) {
            throw new BizException(40400, "图片不存在");
        }

        return photo;
    }

    /**
     * 查询未删除的相册
     */
    private PhotoAlbum getAlbumOrThrow(Long albumId) {
        if (albumId == null || albumId < 1) {
            throw new BizException(40000, "相册 ID 不正确");
        }

        PhotoAlbum album = photoAlbumMapper.selectById(albumId);

        if (album == null
                || Integer.valueOf(DELETED)
                .equals(album.getIsDeleted())) {
            throw new BizException(40400, "相册不存在");
        }

        return album;
    }

    /**
     * 重新查询图片数据
     */
    private PhotoDTO loadPhoto(Long photoId) {
        PhotoDTO photo = photoMapper.selectAdminPhotoById(photoId);

        if (photo == null) {
            throw new BizException(
                    50000,
                    "图片数据读取失败，请稍后重试"
            );
        }

        return photo;
    }

    /**
     * 校验必填文本
     */
    private String normalizeRequiredText(
            String value,
            String message
    ) {
        if (value == null || value.trim().isEmpty()) {
            throw new BizException(40000, message);
        }

        return value.trim();
    }

    /**
     * 清理可选文本
     */
    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();

        return normalizedValue.isEmpty() ? null : normalizedValue;
    }

    /**
     * 规范化当前页码
     */
    private long normalizeCurrent(long current) {
        return current < 1 ? 1 : current;
    }

    /**
     * 规范化分页大小
     */
    private long normalizeSize(long size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            return MAX_SIZE;
        }

        return size;
    }
}