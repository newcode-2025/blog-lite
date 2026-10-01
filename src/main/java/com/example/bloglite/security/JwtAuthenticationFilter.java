package com.example.bloglite.security;

import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.service.JwtService;
import com.example.bloglite.service.UserAccountService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

/**
 * JWT 认证过滤器
 * 负责从 Authorization 请求头中读取并校验 token
 */
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final int STATUS_ENABLED = 1;
    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";

    private final JwtService jwtService;
    private final UserAccountService userAccountService;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserAccountService userAccountService
    ) {
        this.jwtService = jwtService;
        this.userAccountService = userAccountService;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        String authorization = request.getHeader(AUTHORIZATION_HEADER);

        // 没有 Bearer token 时直接继续，由 Spring Security 决定接口是否允许匿名访问
        if (authorization == null
                || !authorization.regionMatches(
                true,
                0,
                BEARER_PREFIX,
                0,
                BEARER_PREFIX.length()
        )) {
            filterChain.doFilter(request, response);
            return;
        }

        String token = authorization
                .substring(BEARER_PREFIX.length())
                .trim();

        try {
            // 验证签名、签发者和过期时间，并解析用户 ID
            Long userId = jwtService.parseUserId(token);

            // 根据用户 ID 查询数据库中的最新用户状态
            UserDTO user = userAccountService.getUserById(userId);

            if (!Integer.valueOf(STATUS_ENABLED).equals(user.getStatus())) {
                SecurityContextHolder.clearContext();
                filterChain.doFilter(request, response);
                return;
            }

            /*
             * 认证凭证的第一个参数是 principal。
             * 这里直接把脱敏 UserDTO 作为当前登录用户。
             */
            UsernamePasswordAuthenticationToken authentication =
                    new UsernamePasswordAuthenticationToken(
                            user,
                            null,
                            Collections.emptyList()
                    );

            authentication.setDetails(
                    new WebAuthenticationDetailsSource()
                            .buildDetails(request)
            );

            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);
        } catch (BizException exception) {
            /*
             * token 无效、过期或用户不存在时清理认证上下文。
             * 受保护接口随后会由 SecurityConfig 的入口返回 401。
             */
            SecurityContextHolder.clearContext();
        }

        filterChain.doFilter(request, response);
    }
}