package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bloglite.dto.LoginRequest;
import com.example.bloglite.dto.RegisterRequest;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.UserAccount;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.UserAccountMapper;
import com.example.bloglite.service.UserAccountService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

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
            throw new BizException(40000, "注册请求不能为空");
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
            throw new BizException(40900, "用户名已存在");
        }

        // 邮箱不为空时再检查邮箱是否已经存在
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

        // 数据库唯一索引负责最终防重
        int affectedRows = userAccountMapper.insert(userAccount);

        if (affectedRows != 1) {
            throw new BizException(50000, "注册失败，请稍后重试");
        }

        return toUserDTO(userAccount);
    }

    @Override
    public UserDTO login(LoginRequest request) {
        if (request == null) {
            throw new BizException(40000, "登录请求不能为空");
        }

        // 注册时已经去除用户名前后空格，登录时保持一致
        String username = request.getUsername().trim();

        UserAccount userAccount = userAccountMapper.selectOne(
                new LambdaQueryWrapper<UserAccount>()
                        .eq(UserAccount::getUsername, username)
        );

        /*
         * 两种情况统一返回相同提示：
         * 1. 用户名不存在
         * 2. 密码错误
         *
         * 这样可以避免攻击者通过错误信息确认某个用户名是否存在。
         */
        if (userAccount == null
                || userAccount.getPasswordHash() == null
                || !passwordEncoder.matches(
                request.getPassword(),
                userAccount.getPasswordHash()
        )) {
            throw new BizException(40100, "用户名或密码错误");
        }

        // 只有密码验证通过后，才明确告诉用户账号被禁用
        if (!Integer.valueOf(STATUS_ENABLED).equals(userAccount.getStatus())) {
            throw new BizException(40300, "账号已被禁用");
        }

        LocalDateTime now = LocalDateTime.now();

        /*
         * 登录成功只更新最后登录时间和更新时间。
         * MyBatis-Plus 默认会忽略实体中的 null 字段，
         * 因此不会误改用户名、密码哈希等字段。
         */
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

        userAccount.setLastLoginAt(now);
        userAccount.setUpdatedAt(now);

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