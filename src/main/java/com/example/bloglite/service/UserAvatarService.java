package com.example.bloglite.service;

import com.example.bloglite.dto.UserDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * 用户头像业务接口
 */
public interface UserAvatarService {

    /**
     * 上传并更新当前用户头像
     */
    UserDTO updateAvatar(Long userId, MultipartFile file);
}
