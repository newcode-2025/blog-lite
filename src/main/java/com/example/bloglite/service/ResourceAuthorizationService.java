package com.example.bloglite.service;

import java.util.Collection;

/**
 * 接口资源授权服务
 */
public interface ResourceAuthorizationService {

    /**
     * 根据请求方法和路径判断当前请求是否允许访问
     */
    AuthorizationDecision authorize(
            String httpMethod,
            String requestUri,
            boolean authenticated,
            Collection<String> authorities
    );

    /**
     * 动态授权结果
     */
    record AuthorizationDecision(
            boolean allowed,
            boolean requiresAuthentication,
            String message
    ) {

        public static AuthorizationDecision allow() {
            return new AuthorizationDecision(true, false, null);
        }

        public static AuthorizationDecision unauthenticated() {
            return new AuthorizationDecision(
                    false,
                    true,
                    "未登录或登录状态已过期"
            );
        }

        public static AuthorizationDecision denied(String message) {
            return new AuthorizationDecision(false, false, message);
        }
    }
}
