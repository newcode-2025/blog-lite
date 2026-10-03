package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.CurrentUserRoleDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.util.UserContext;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 当前登录用户角色查询接口
 */
@RestController
@RequestMapping("/api/v1/users/me/roles")
public class CurrentUserRoleController {

    private static final String ROLE_PREFIX = "ROLE_";

    /**
     * 返回 Spring Security 当前请求中已经加载的角色权限
     */
    @GetMapping
    public ApiResponse<CurrentUserRoleDTO> getCurrentUserRoles() {
        UserDTO user = UserContext.getCurrentUser();
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        List<String> authorities = authentication.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .sorted()
                .toList();

        List<String> roleCodes = authorities.stream()
                .filter(authority -> authority.startsWith(ROLE_PREFIX))
                .map(authority -> authority.substring(ROLE_PREFIX.length()))
                .toList();

        return ApiResponse.success(
                new CurrentUserRoleDTO(
                        user.getId(),
                        user.getUsername(),
                        roleCodes,
                        authorities
                )
        );
    }
}
