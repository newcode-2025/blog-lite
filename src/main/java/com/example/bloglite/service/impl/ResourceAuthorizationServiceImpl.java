package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.entity.ApiResource;
import com.example.bloglite.entity.Role;
import com.example.bloglite.entity.RoleResource;
import com.example.bloglite.mapper.ApiResourceMapper;
import com.example.bloglite.mapper.RoleMapper;
import com.example.bloglite.mapper.RoleResourceMapper;
import com.example.bloglite.service.ResourceAuthorizationService;
import org.springframework.stereotype.Service;
import org.springframework.util.AntPathMatcher;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 接口资源授权实现
 */
@Service
public class ResourceAuthorizationServiceImpl
        implements ResourceAuthorizationService {

    private static final int STATUS_ENABLED = 1;
    private static final int ANONYMOUS_YES = 1;
    private static final String ROLE_PREFIX = "ROLE_";
    private static final String ADMIN_PATH = "/api/v1/admin";

    private final ApiResourceMapper apiResourceMapper;
    private final RoleResourceMapper roleResourceMapper;
    private final RoleMapper roleMapper;
    private final AntPathMatcher antPathMatcher = new AntPathMatcher();

    public ResourceAuthorizationServiceImpl(
            ApiResourceMapper apiResourceMapper,
            RoleResourceMapper roleResourceMapper,
            RoleMapper roleMapper
    ) {
        this.apiResourceMapper = apiResourceMapper;
        this.roleResourceMapper = roleResourceMapper;
        this.roleMapper = roleMapper;
    }

    @Override
    public AuthorizationDecision authorize(
            String httpMethod,
            String requestUri,
            boolean authenticated,
            Collection<String> authorities
    ) {
        if (httpMethod == null || requestUri == null) {
            return AuthorizationDecision.denied("请求信息不完整");
        }

        ApiResource matchedResource = findMatchedResource(
                httpMethod,
                requestUri
        );

        if (matchedResource == null) {
            if (isAdminPath(requestUri)) {
                return authenticated
                        ? AuthorizationDecision.denied(
                                "未配置对应的后台接口资源"
                        )
                        : AuthorizationDecision.unauthenticated();
            }

            if (isPublicPath(httpMethod, requestUri)) {
                return AuthorizationDecision.allow();
            }

            return authenticated
                    ? AuthorizationDecision.allow()
                    : AuthorizationDecision.unauthenticated();
        }

        if (Integer.valueOf(ANONYMOUS_YES)
                .equals(matchedResource.getIsAnonymous())) {
            return AuthorizationDecision.allow();
        }

        if (!authenticated) {
            return AuthorizationDecision.unauthenticated();
        }

        if (authorities == null || authorities.isEmpty()) {
            return AuthorizationDecision.denied("无权访问");
        }

        Set<String> requiredAuthorities = loadRequiredAuthorities(
                matchedResource.getId()
        );

        if (requiredAuthorities.isEmpty()) {
            return AuthorizationDecision.denied(
                    "接口资源未分配可访问角色"
            );
        }

        boolean hasPermission = authorities.stream()
                .anyMatch(requiredAuthorities::contains);

        if (!hasPermission) {
            return AuthorizationDecision.denied("无权访问");
        }

        return AuthorizationDecision.allow();
    }

    private ApiResource findMatchedResource(
            String httpMethod,
            String requestUri
    ) {
        List<ApiResource> resources = apiResourceMapper.selectList(
                Wrappers.<ApiResource>lambdaQuery()
                        .eq(ApiResource::getStatus, STATUS_ENABLED)
                        .isNotNull(ApiResource::getUrlPattern)
                        .isNotNull(ApiResource::getHttpMethod)
        );

        return resources.stream()
                .filter(resource -> httpMethod.equalsIgnoreCase(
                        resource.getHttpMethod()
                ))
                .filter(resource -> antPathMatcher.match(
                        resource.getUrlPattern(),
                        requestUri
                ))
                .sorted(resourceSpecificityComparator())
                .findFirst()
                .orElse(null);
    }

    private Comparator<ApiResource> resourceSpecificityComparator() {
        return Comparator
                .comparingInt((ApiResource resource) ->
                        wildcardCount(resource.getUrlPattern())
                )
                .thenComparing(
                        (ApiResource resource) ->
                                resource.getUrlPattern().length(),
                        Comparator.reverseOrder()
                )
                .thenComparing(
                        ApiResource::getId,
                        Comparator.naturalOrder()
                );
    }

    private int wildcardCount(String urlPattern) {
        int count = 0;

        for (int index = 0; index < urlPattern.length(); index++) {
            char current = urlPattern.charAt(index);

            if (current == '*' || current == '?') {
                count++;
            }
        }

        return count;
    }

    private Set<String> loadRequiredAuthorities(Long resourceId) {
        List<Long> roleIds = roleResourceMapper.selectList(
                        Wrappers.<RoleResource>lambdaQuery()
                                .eq(
                                        RoleResource::getResourceId,
                                        resourceId
                                )
                )
                .stream()
                .map(RoleResource::getRoleId)
                .distinct()
                .toList();

        if (roleIds.isEmpty()) {
            return Set.of();
        }

        return roleMapper.selectList(
                        Wrappers.<Role>lambdaQuery()
                                .in(Role::getId, roleIds)
                                .eq(Role::getStatus, STATUS_ENABLED)
                )
                .stream()
                .map(Role::getRoleCode)
                .filter(roleCode ->
                        roleCode != null && !roleCode.isBlank()
                )
                .map(roleCode -> ROLE_PREFIX + roleCode)
                .collect(Collectors.toSet());
    }

    private boolean isPublicPath(String httpMethod, String requestUri) {
        if ("OPTIONS".equalsIgnoreCase(httpMethod)) {
            return true;
        }

        if (requestUri.equals("/api/v1/health")
                || requestUri.startsWith("/error")
                || requestUri.startsWith("/api/v1/auth/")) {
            return true;
        }

        if (!"GET".equalsIgnoreCase(httpMethod)) {
            return false;
        }

        return requestUri.equals("/api/v1/articles")
                || requestUri.startsWith("/api/v1/articles/")
                || requestUri.equals("/api/v1/categories")
                || requestUri.startsWith("/api/v1/categories/")
                || requestUri.equals("/api/v1/tags")
                || requestUri.startsWith("/api/v1/tags/")
                || requestUri.equals("/api/v1/archives")
                || requestUri.startsWith("/api/v1/archives/")
                || requestUri.equals("/api/v1/talks")
                || requestUri.startsWith("/api/v1/talks/")
                || requestUri.equals("/api/v1/links")
                || requestUri.startsWith("/api/v1/links/")
                || requestUri.equals("/api/v1/albums")
                || requestUri.startsWith("/api/v1/albums/")
                || requestUri.equals("/api/v1/comments")
                || requestUri.startsWith("/api/v1/comments/");
    }
    private boolean isAdminPath(String requestUri) {
        return ADMIN_PATH.equals(requestUri)
                || requestUri.startsWith(ADMIN_PATH + "/");
    }
}
