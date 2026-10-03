package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.MediaUploadResultDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.UserAccount;
import com.example.bloglite.enums.MediaScene;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.UserAccountMapper;
import com.example.bloglite.service.MediaStorageService;
import com.example.bloglite.service.UserAvatarService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

/**
 * 用户头像业务实现
 */
@Service
public class UserAvatarServiceImpl implements UserAvatarService {

    private static final int STATUS_ENABLED = 1;

    private final UserAccountMapper userAccountMapper;

    private final MediaStorageService mediaStorageService;

    public UserAvatarServiceImpl(
            UserAccountMapper userAccountMapper,
            MediaStorageService mediaStorageService
    ) {
        this.userAccountMapper = userAccountMapper;
        this.mediaStorageService = mediaStorageService;
    }

    @Transactional
    @Override
    public UserDTO updateAvatar(Long userId, MultipartFile file) {
        if (userId == null || userId < 1) {
            throw new BizException(40000, "用户 ID 不正确");
        }

        UserAccount userAccount = userAccountMapper.selectById(userId);

        if (userAccount == null) {
            throw new BizException(40400, "用户不存在");
        }

        if (!Integer.valueOf(STATUS_ENABLED)
                .equals(userAccount.getStatus())) {
            throw new BizException(40300, "账号已被禁用");
        }

        MediaUploadResultDTO uploadResult = mediaStorageService.upload(
                file,
                MediaScene.AVATAR
        );

        try {
            int updatedRows = userAccountMapper.update(
                    null,
                    new LambdaUpdateWrapper<UserAccount>()
                            .eq(UserAccount::getId, userId)
                            .eq(UserAccount::getStatus, STATUS_ENABLED)
                            .set(
                                    UserAccount::getAvatarUrl,
                                    uploadResult.getUrl()
                            )
                            .set(
                                    UserAccount::getUpdatedAt,
                                    LocalDateTime.now()
                            )
            );

            if (updatedRows != 1) {
                throw new BizException(
                        40900,
                        "头像更新失败，账号状态可能已变化"
                );
            }

            UserAccount updatedUser = userAccountMapper.selectById(userId);

            if (updatedUser == null) {
                throw new BizException(40400, "用户不存在");
            }

            return toUserDTO(updatedUser);
        } catch (RuntimeException exception) {
            deleteUploadedAvatarQuietly(uploadResult.getUrl());
            throw exception;
        }
    }

    private void deleteUploadedAvatarQuietly(String url) {
        try {
            mediaStorageService.deleteByUrl(url);
        } catch (RuntimeException ignored) {
            // 头像更新失败时尽力回收已经上传的文件
        }
    }

    private UserDTO toUserDTO(UserAccount userAccount) {
        return new UserDTO(
                userAccount.getId(),
                userAccount.getUsername(),
                userAccount.getNickname(),
                userAccount.getEmail(),
                userAccount.getAvatarUrl(),
                userAccount.getStatus(),
                userAccount.getCreatedAt()
        );
    }
}
