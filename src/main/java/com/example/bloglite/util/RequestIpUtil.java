package com.example.bloglite.util;

import jakarta.servlet.http.HttpServletRequest;

/**
 * 请求 IP 工具类
 */
public final class RequestIpUtil {

    private static final int MAX_IP_LENGTH = 64;

    private RequestIpUtil() {
    }

    /**
     * 获取客户端 IP
     *
     * 当前项目本地运行，优先读取反向代理常用请求头。
     * 如果部署到公网并启用代理，必须只信任受控代理写入的转发头。
     */
    public static String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        String forwardedFor = request.getHeader("X-Forwarded-For");

        if (forwardedFor != null && !forwardedFor.isBlank()) {
            String firstIp = forwardedFor.split(",", 2)[0].trim();
            return limit(firstIp);
        }

        String realIp = request.getHeader("X-Real-IP");

        if (realIp != null && !realIp.isBlank()) {
            return limit(realIp.trim());
        }

        return limit(request.getRemoteAddr());
    }

    private static String limit(String value) {
        if (value == null) {
            return "";
        }

        String normalized = value.trim();
        return normalized.length() <= MAX_IP_LENGTH
                ? normalized
                : normalized.substring(0, MAX_IP_LENGTH);
    }
}