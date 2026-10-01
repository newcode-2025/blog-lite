package com.example.bloglite.util;

import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.exception.BizException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 当前登录用户工具类
 */
public final class UserContext {

    private UserContext() {
    }

    /**
     * 获取当前登录用户
     */
    public static UserDTO getCurrentUser() {
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserDTO user)) {
            throw new BizException(
                    40100,
                    "未登录或登录状态已过期"
            );
        }

        return user;
    }
}