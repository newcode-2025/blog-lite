package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.AdminPhotoAlbumSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.PhotoAlbumDTO;
import com.example.bloglite.entity.PhotoAlbum;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.PhotoAlbumMapper;
import com.example.bloglite.service.PhotoAlbumService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 相册业务实现
 */
@Service
public class PhotoAlbumServiceImpl implements PhotoAlbumService {

    private static final int STATUS_PUBLIC = 1;
    private static final int STATUS_PRIVATE = 2;

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private static final int DEFAULT_SORT_ORDER = 0;

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private final PhotoAlbumMapper photoAlbumMapper;

    public PhotoAlbumServiceImpl(
            PhotoAlbumMapper photoAlbumMapper
    ) {
        this.photoAlbumMapper = photoAlbumMapper;
    }

    /**
     * 查询全部公开相册
     */
    @Override
    public List<PhotoAlbumDTO> listPublicAlbums() {
        return photoAlbumMapper.selectPublicAlbums();
    }

    /**
     * 后台分页查询相册
     */
    @Override
    public PageResultDTO<PhotoAlbumDTO> listAdminAlbums(
            Integer status,
            String keywords,
            long current,
            long size
    ) {
        validateStatus(status);

        current = normalizeCurrent(current);
        size = normalizeSize(size);

        String normalizedKeywords = normalizeKeywords(keywords);

        long offset = (current - 1) * size;

        List<PhotoAlbumDTO> records =
                photoAlbumMapper.selectAdminAlbums(
                        status,
                        normalizedKeywords,
                        offset,
                        size
                );

        long count = photoAlbumMapper.countAdminAlbums(
                status,
                normalizedKeywords
        );

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    /**
     * 新建相册
     */
    @Transactional
    @Override
    public PhotoAlbumDTO createAlbum(
            AdminPhotoAlbumSaveRequest request
    ) {
        String name = normalizeRequiredText(
                request.getName(),
                "相册名称不能为空"
        );

        ensureNameAvailable(name, null);

        int sortOrder = request.getSortOrder() == null
                ? DEFAULT_SORT_ORDER
                : request.getSortOrder();

        int status = request.getStatus() == null
                ? STATUS_PUBLIC
                : normalizeStatus(request.getStatus());

        LocalDateTime now = LocalDateTime.now();

        PhotoAlbum album = new PhotoAlbum();
        album.setName(name);
        album.setDescription(
                normalizeOptionalText(request.getDescription())
        );
        album.setCoverUrl(
                normalizeOptionalText(request.getCoverUrl())
        );
        album.setSortOrder(sortOrder);
        album.setStatus(status);
        album.setIsDeleted(NOT_DELETED);
        album.setCreatedAt(now);
        album.setUpdatedAt(now);

        int insertedRows;

        try {
            insertedRows = photoAlbumMapper.insert(album);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "相册名称已存在"
            );
        }

        if (insertedRows != 1 || album.getId() == null) {
            throw new BizException(
                    50000,
                    "相册创建失败，请稍后重试"
            );
        }

        return loadAlbum(album.getId());
    }

    /**
     * 编辑相册
     */
    @Transactional
    @Override
    public PhotoAlbumDTO updateAlbum(
            Long albumId,
            AdminPhotoAlbumSaveRequest request
    ) {
        PhotoAlbum existingAlbum = getAlbumOrThrow(albumId);

        String name = normalizeRequiredText(
                request.getName(),
                "相册名称不能为空"
        );

        ensureNameAvailable(name, albumId);

        int sortOrder = request.getSortOrder() == null
                ? existingAlbum.getSortOrder()
                : request.getSortOrder();

        int status = request.getStatus() == null
                ? existingAlbum.getStatus()
                : normalizeStatus(request.getStatus());

        PhotoAlbum updateAlbum = new PhotoAlbum();
        updateAlbum.setId(albumId);
        updateAlbum.setName(name);
        updateAlbum.setDescription(
                normalizeOptionalText(request.getDescription())
        );
        updateAlbum.setCoverUrl(
                normalizeOptionalText(request.getCoverUrl())
        );
        updateAlbum.setSortOrder(sortOrder);
        updateAlbum.setStatus(status);
        updateAlbum.setUpdatedAt(LocalDateTime.now());

        int updatedRows;

        try {
            updatedRows = photoAlbumMapper.updateById(updateAlbum);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "相册名称已存在"
            );
        }

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "相册更新失败，请稍后重试"
            );
        }

        return loadAlbum(albumId);
    }

    /**
     * 软删除相册及其图片
     */
    @Transactional
    @Override
    public void deleteAlbum(Long albumId) {
        getAlbumOrThrow(albumId);

        photoAlbumMapper.softDeletePhotosByAlbumId(albumId);

        LambdaUpdateWrapper<PhotoAlbum> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(PhotoAlbum::getId, albumId)
                .eq(PhotoAlbum::getIsDeleted, NOT_DELETED)
                .set(PhotoAlbum::getIsDeleted, DELETED)
                .set(
                        PhotoAlbum::getUpdatedAt,
                        LocalDateTime.now()
                );

        int updatedRows = photoAlbumMapper.update(
                null,
                updateWrapper
        );

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "相册删除失败，请稍后重试"
            );
        }
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
     * 重新查询相册数据
     */
    private PhotoAlbumDTO loadAlbum(Long albumId) {
        PhotoAlbumDTO album =
                photoAlbumMapper.selectAdminAlbumById(albumId);

        if (album == null) {
            throw new BizException(
                    50000,
                    "相册数据读取失败，请稍后重试"
            );
        }

        return album;
    }

    /**
     * 校验列表状态
     */
    private void validateStatus(Integer status) {
        if (status != null) {
            normalizeStatus(status);
        }
    }

    /**
     * 校验并返回相册状态
     */
    private int normalizeStatus(Integer status) {
        if (status == null
                || (status != STATUS_PUBLIC
                && status != STATUS_PRIVATE)) {
            throw new BizException(
                    40000,
                    "相册状态只能是 1 或 2"
            );
        }

        return status;
    }

    /**
     * 校验相册名称是否重复
     */
    private void ensureNameAvailable(
            String name,
            Long excludeId
    ) {
        long count = photoAlbumMapper.countByName(
                name,
                excludeId
        );

        if (count > 0) {
            throw new BizException(
                    40900,
                    "相册名称已存在"
            );
        }
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
     * 规范化搜索关键词
     */
    private String normalizeKeywords(String keywords) {
        if (keywords == null) {
            return null;
        }

        String normalizedKeywords = keywords.trim();

        return normalizedKeywords.isEmpty()
                ? null
                : normalizedKeywords;
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