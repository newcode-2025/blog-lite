package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.LoginDTO;
import com.example.bloglite.dto.LoginRequest;
import com.example.bloglite.dto.RegisterRequest;
import com.example.bloglite.dto.UpdateProfileRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.UserAccount;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.UserAccountMapper;
import com.example.bloglite.service.JwtService;
import com.example.bloglite.service.UserAccountService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * 用户账号业务实现
 */
@Service
public class UserAccountServiceImpl implements UserAccountService {

    /**
     * 正常状态
     */
    private static final int STATUS_ENABLED = 1;

    private final UserAccountMapper userAccountMapper;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserAccountServiceImpl(
            UserAccountMapper userAccountMapper,
            PasswordEncoder passwordEncoder,
            JwtService jwtService
    ) {
        this.userAccountMapper = userAccountMapper;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Override
    public UserDTO register(RegisterRequest request) {
        if (request == null) {
            throw new BizException(40000, "注册请求不能为空");
        }

        String username = request.getUsername().trim();
        String email = normalizeNullableText(request.getEmail());

        Long usernameCount = userAccountMapper.selectCount(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getUsername, username)
        );

        if (usernameCount > 0) {
            throw new BizException(40900, "用户名已存在");
        }

        if (email != null) {
            Long emailCount = userAccountMapper.selectCount(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getEmail, email)
            );

            if (emailCount > 0) {
                throw new BizException(40900, "邮箱已被注册");
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
        userAccount.setStatus(STATUS_ENABLED);
        userAccount.setCreatedAt(now);
        userAccount.setUpdatedAt(now);

        int affectedRows = userAccountMapper.insert(userAccount);

        if (affectedRows != 1) {
            throw new BizException(50000, "注册失败，请稍后重试");
        }

        return toUserDTO(userAccount);
    }

    @Transactional
    @Override
    public LoginDTO login(LoginRequest request) {
        if (request == null) {
            throw new BizException(40000, "登录请求不能为空");
        }

        String username = request.getUsername().trim();

        UserAccount userAccount = userAccountMapper.selectOne(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getUsername, username)
        );

        /*
         * 用户名不存在和密码错误使用相同提示，
         * 避免攻击者确认某个用户名是否已经存在。
         */
        if (userAccount == null
                || userAccount.getPasswordHash() == null
                || !passwordEncoder.matches(
                request.getPassword(),
                userAccount.getPasswordHash()
        )) {
            throw new BizException(40100, "用户名或密码错误");
        }

        if (!Integer.valueOf(STATUS_ENABLED).equals(userAccount.getStatus())) {
            throw new BizException(40300, "账号已被禁用");
        }

        LocalDateTime now = LocalDateTime.now();

        UserAccount loginUpdate = new UserAccount();
        loginUpdate.setId(userAccount.getId());
        loginUpdate.setLastLoginAt(now);
        loginUpdate.setUpdatedAt(now);

        int affectedRows = userAccountMapper.updateById(loginUpdate);

        if (affectedRows != 1) {
            throw new BizException(
                    50000,
                    "登录状态更新失败，请稍后重试"
            );
        }

        String token = jwtService.createToken(userAccount.getId());

        userAccount.setLastLoginAt(now);
        userAccount.setUpdatedAt(now);

        return new LoginDTO(
                token,
                "Bearer",
                jwtService.getExpirationSeconds(),
                toUserDTO(userAccount)
        );
    }

    @Override
    public UserDTO getUserById(Long userId) {
        if (userId == null || userId < 1) {
            throw new BizException(40000, "用户 ID 不正确");
        }

        UserAccount userAccount = userAccountMapper.selectById(userId);

        if (userAccount == null) {
            throw new BizException(40400, "用户不存在");
        }

        return toUserDTO(userAccount);
    }

    @Transactional
    @Override
    public UserDTO updateCurrentUser(
            Long userId,
            UpdateProfileRequest request
    ) {
        if (userId == null || userId < 1) {
            throw new BizException(40000, "用户 ID 不正确");
        }

        if (request == null) {
            throw new BizException(40000, "修改资料请求不能为空");
        }

        UserAccount userAccount = userAccountMapper.selectById(userId);

        if (userAccount == null) {
            throw new BizException(40400, "用户不存在");
        }

        if (!Integer.valueOf(STATUS_ENABLED).equals(userAccount.getStatus())) {
            throw new BizException(40300, "账号已被禁用");
        }

        if (request.getNickname() == null
                || request.getNickname().trim().isEmpty()) {
            throw new BizException(40000, "昵称不能为空");
        }

        String nickname = request.getNickname().trim();
        String email = normalizeNullableText(request.getEmail());
        String avatarUrl = normalizeNullableText(request.getAvatarUrl());

        if (email != null) {
            Long emailCount = userAccountMapper.selectCount(
                    new LambdaQueryWrapper<UserAccount>()
                            .eq(UserAccount::getEmail, email)
                            .ne(UserAccount::getId, userId)
            );

            if (emailCount > 0) {
                throw new BizException(40900, "邮箱已被注册");
            }
        }

        LocalDateTime now = LocalDateTime.now();

        /*
         * 使用 LambdaUpdateWrapper 显式写入 null，
         * 这样空邮箱和空头像可以被真正清空。
         */
        LambdaUpdateWrapper<UserAccount> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(UserAccount::getId, userId)
                .set(UserAccount::getNickname, nickname)
                .set(UserAccount::getEmail, email)
                .set(UserAccount::getAvatarUrl, avatarUrl)
                .set(UserAccount::getUpdatedAt, now);

        try {
            userAccountMapper.update(null, updateWrapper);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "邮箱已被注册",
                    exception
            );
        }

        UserAccount updatedUser = userAccountMapper.selectById(userId);

        if (updatedUser == null) {
            throw new BizException(40400, "用户不存在");
        }

        return toUserDTO(updatedUser);
    }

    /**
     * 将空字符串统一转换为 null
     */
    private String normalizeNullableText(String value) {
        if (value == null) {
            return null;
        }

        String trimmedValue = value.trim();

        if (trimmedValue.isEmpty()) {
            return null;
        }

        return trimmedValue;
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