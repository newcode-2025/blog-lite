package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bloglite.dto.RegisterRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.UserAccount;
import com.example.bloglite.mapper.UserAccountMapper;
import com.example.bloglite.service.UserAccountService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

/**
 * 用户账号业务实现
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;

    public UserAccountServiceImpl(
            UserAccountMapper userAccountMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.userAccountMapper = userAccountMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDTO register(RegisterRequest request) {
        if (request == null) {
            throw new IllegalArgumentException("注册请求不能为空");
        }

        // 去除用户名前后的空格
        String username = request.getUsername().trim();

        // 将空邮箱统一转换为 null
        String email = normalizeEmail(request.getEmail());

        // 先查询用户名是否已经存在
        Long usernameCount = userAccountMapper.selectCount(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getUsername, username)
        );

        if (usernameCount > 0) {
            throw new IllegalArgumentException("用户名已存在");
        }

        // 邮箱不为空时再检查邮箱是否已经存在
        if (email != null) {
            Long emailCount = userAccountMapper.selectCount(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getEmail, email)
            );

            if (emailCount > 0) {
                throw new IllegalArgumentException("邮箱已被注册");
            }
        }

        LocalDateTime now = LocalDateTime.now();

        UserAccount userAccount = new UserAccount();
        userAccount.setUsername(username);

        // 只保存 BCrypt 哈希，不保存原始密码
        userAccount.setPasswordHash(
                passwordEncoder.encode(request.getPassword())
        );

        userAccount.setNickname(request.getNickname().trim());
        userAccount.setEmail(email);
        userAccount.setStatus(1);
        userAccount.setCreatedAt(now);
        userAccount.setUpdatedAt(now);

        try {
            userAccountMapper.insert(userAccount);
        } catch (DuplicateKeyException exception) {
            // 兜底处理数据库唯一索引冲突
            throw new IllegalArgumentException(
                    "用户名或邮箱已存在",
                    exception
            );
        }

        return toUserDTO(userAccount);
    }

    /**
     * 将空邮箱统一转换为 null
     */
    private String normalizeEmail(String email) {
        if (email == null) {
            return null;
        }

        String trimmedEmail = email.trim();

        if (trimmedEmail.isEmpty()) {
            return null;
        }

        return trimmedEmail;
    }

    /**
     * 将实体转换为脱敏 DTO
     */
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