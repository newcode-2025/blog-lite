package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.dto.AdminResourceDTO;
import com.example.bloglite.dto.AdminResourceSaveRequest;
import com.example.bloglite.entity.ApiResource;
import com.example.bloglite.entity.RoleResource;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ApiResourceMapper;
import com.example.bloglite.mapper.RoleResourceMapper;
import com.example.bloglite.service.AdminResourceService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

/**
 * 后台接口资源业务实现
 */
@Service
public class AdminResourceServiceImpl implements AdminResourceService {

    private static final Set<String> SUPPORTED_HTTP_METHODS = Set.of(
            "GET",
            "POST",
            "PUT",
            "PATCH",
            "DELETE",
            "HEAD",
            "OPTIONS"
    );

    private final ApiResourceMapper apiResourceMapper;
    private final RoleResourceMapper roleResourceMapper;

    public AdminResourceServiceImpl(
            ApiResourceMapper apiResourceMapper,
            RoleResourceMapper roleResourceMapper
    ) {
        this.apiResourceMapper = apiResourceMapper;
        this.roleResourceMapper = roleResourceMapper;
    }

    @Override
    public List<AdminResourceDTO> listResourceTree() {
        return buildTree(loadAllResources());
    }

    @Override
    public AdminResourceDTO getResourceDetail(Long resourceId) {
        return toDTO(getResourceOrThrow(resourceId));
    }

    @Transactional
    @Override
    public AdminResourceDTO createResource(
            AdminResourceSaveRequest request
    ) {
        NormalizedResource normalizedResource =
                normalizeRequest(request, null);
        ensureNameAvailable(
                normalizedResource.resourceName(),
                normalizedResource.parentId(),
                null
        );
        ensureRuleAvailable(
                normalizedResource.httpMethod(),
                normalizedResource.urlPattern(),
                null
        );

        LocalDateTime now = LocalDateTime.now();

        ApiResource resource = new ApiResource();
        resource.setResourceName(normalizedResource.resourceName());
        resource.setUrlPattern(normalizedResource.urlPattern());
        resource.setHttpMethod(normalizedResource.httpMethod());
        resource.setParentId(normalizedResource.parentId());
        resource.setIsAnonymous(normalizedResource.isAnonymous());
        resource.setStatus(normalizedResource.status());
        resource.setSortOrder(normalizedResource.sortOrder());
        resource.setCreatedAt(now);
        resource.setUpdatedAt(now);

        int insertedRows;

        try {
            insertedRows = apiResourceMapper.insert(resource);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "相同 HTTP 方法和 URL 规则的接口资源已存在",
                    exception
            );
        }

        if (insertedRows != 1 || resource.getId() == null) {
            throw new BizException(
                    50000,
                    "接口资源创建失败，请稍后重试"
            );
        }

        return getResourceDetail(resource.getId());
    }

    @Transactional
    @Override
    public AdminResourceDTO updateResource(
            Long resourceId,
            AdminResourceSaveRequest request
    ) {
        getResourceOrThrow(resourceId);
        NormalizedResource normalizedResource =
                normalizeRequest(request, resourceId);

        ensureNameAvailable(
                normalizedResource.resourceName(),
                normalizedResource.parentId(),
                resourceId
        );
        ensureRuleAvailable(
                normalizedResource.httpMethod(),
                normalizedResource.urlPattern(),
                resourceId
        );

        ApiResource resource = new ApiResource();
        resource.setId(resourceId);
        resource.setResourceName(normalizedResource.resourceName());
        resource.setUrlPattern(normalizedResource.urlPattern());
        resource.setHttpMethod(normalizedResource.httpMethod());
        resource.setParentId(normalizedResource.parentId());
        resource.setIsAnonymous(normalizedResource.isAnonymous());
        resource.setStatus(normalizedResource.status());
        resource.setSortOrder(normalizedResource.sortOrder());
        resource.setUpdatedAt(LocalDateTime.now());

        int updatedRows;

        try {
            updatedRows = apiResourceMapper.updateById(resource);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "相同 HTTP 方法和 URL 规则的接口资源已存在",
                    exception
            );
        }

        if (updatedRows != 1) {
            throw new BizException(
                    50000,
                    "接口资源更新失败，请稍后重试"
            );
        }

        return getResourceDetail(resourceId);
    }

    @Transactional
    @Override
    public void deleteResource(Long resourceId) {
        getResourceOrThrow(resourceId);

        long childCount = apiResourceMapper.selectCount(
                Wrappers.<ApiResource>lambdaQuery()
                        .eq(ApiResource::getParentId, resourceId)
        );

        if (childCount > 0) {
            throw new BizException(
                    40900,
                    "接口资源存在子资源，不能删除"
            );
        }

        long roleResourceCount = roleResourceMapper.selectCount(
                Wrappers.<RoleResource>lambdaQuery()
                        .eq(RoleResource::getResourceId, resourceId)
        );

        if (roleResourceCount > 0) {
            throw new BizException(
                    40900,
                    "接口资源已分配给角色，不能删除"
            );
        }

        int deletedRows = apiResourceMapper.deleteById(resourceId);

        if (deletedRows != 1) {
            throw new BizException(
                    50000,
                    "接口资源删除失败，请稍后重试"
            );
        }
    }

    private List<ApiResource> loadAllResources() {
        return apiResourceMapper.selectList(
                Wrappers.<ApiResource>lambdaQuery()
                        .orderByAsc(ApiResource::getSortOrder)
                        .orderByAsc(ApiResource::getId)
        );
    }

    private List<AdminResourceDTO> buildTree(List<ApiResource> resources) {
        Map<Long, AdminResourceDTO> resourceMap = new LinkedHashMap<>();

        for (ApiResource resource : resources) {
            resourceMap.put(resource.getId(), toDTO(resource));
        }

        List<AdminResourceDTO> roots = new ArrayList<>();

        for (ApiResource resource : resources) {
            AdminResourceDTO current = resourceMap.get(resource.getId());

            if (resource.getParentId() == null) {
                roots.add(current);
                continue;
            }

            AdminResourceDTO parent = resourceMap.get(resource.getParentId());

            if (parent == null) {
                roots.add(current);
                continue;
            }

            parent.getChildren().add(current);
        }

        return roots;
    }

    private NormalizedResource normalizeRequest(
            AdminResourceSaveRequest request,
            Long currentResourceId
    ) {
        if (request == null) {
            throw new BizException(40000, "接口资源请求不能为空");
        }

        String resourceName = normalizeRequiredText(
                request.getResourceName(),
                "资源名称不能为空",
                100
        );

        Integer sortOrder = request.getSortOrder();

        if (sortOrder == null || sortOrder < 0) {
            throw new BizException(40000, "资源排序值不能小于 0");
        }

        Integer isAnonymous = validateBinaryStatus(
                request.getIsAnonymous(),
                "匿名访问状态只能是 0 或 1"
        );

        Integer status = validateBinaryStatus(
                request.getStatus(),
                "资源状态只能是 0 或 1"
        );

        Long parentId = request.getParentId();
        String urlPattern = null;
        String httpMethod = null;

        if (parentId != null) {
            validateParent(currentResourceId, parentId);

            urlPattern = normalizeUrlPattern(request.getUrlPattern());
            httpMethod = normalizeHttpMethod(request.getHttpMethod());
        }

        return new NormalizedResource(
                resourceName,
                urlPattern,
                httpMethod,
                parentId,
                isAnonymous,
                status,
                sortOrder
        );
    }

    private void validateParent(Long currentResourceId, Long parentId) {
        if (currentResourceId != null
                && currentResourceId.equals(parentId)) {
            throw new BizException(40000, "父资源不能是当前资源");
        }

        ApiResource parent = apiResourceMapper.selectById(parentId);

        if (parent == null) {
            throw new BizException(40400, "父资源不存在");
        }

        if (parent.getParentId() != null) {
            throw new BizException(40000, "父资源必须是根节点");
        }

        if (currentResourceId != null) {
            long childCount = apiResourceMapper.selectCount(
                    Wrappers.<ApiResource>lambdaQuery()
                            .eq(ApiResource::getParentId, currentResourceId)
            );

            if (childCount > 0) {
                throw new BizException(
                        40900,
                        "存在子资源，不能移动到其他节点下"
                );
            }
        }

        Set<Long> visited = new HashSet<>();
        Long cursorId = parentId;

        while (cursorId != null) {
            if (currentResourceId != null
                    && currentResourceId.equals(cursorId)) {
                throw new BizException(40900, "接口资源不能形成循环");
            }

            if (!visited.add(cursorId)) {
                throw new BizException(40900, "接口资源父子关系已存在循环");
            }

            ApiResource cursor = cursorId.equals(parentId)
                    ? parent
                    : apiResourceMapper.selectById(cursorId);

            if (cursor == null) {
                throw new BizException(40400, "父资源链中存在不存在的资源");
            }

            cursorId = cursor.getParentId();
        }
    }

    private void ensureNameAvailable(
            String resourceName,
            Long parentId,
            Long excludeResourceId
    ) {
        LambdaQueryWrapper<ApiResource> queryWrapper =
                Wrappers.<ApiResource>lambdaQuery()
                        .eq(ApiResource::getResourceName, resourceName)
                        .ne(
                                excludeResourceId != null,
                                ApiResource::getId,
                                excludeResourceId
                        );

        if (parentId == null) {
            queryWrapper.isNull(ApiResource::getParentId);
        } else {
            queryWrapper.eq(ApiResource::getParentId, parentId);
        }

        long count = apiResourceMapper.selectCount(queryWrapper);

        if (count > 0) {
            throw new BizException(
                    40900,
                    "同一父资源下已存在同名资源"
            );
        }
    }

    private void ensureRuleAvailable(
            String httpMethod,
            String urlPattern,
            Long excludeResourceId
    ) {
        if (httpMethod == null || urlPattern == null) {
            return;
        }

        long count = apiResourceMapper.selectCount(
                Wrappers.<ApiResource>lambdaQuery()
                        .eq(ApiResource::getHttpMethod, httpMethod)
                        .eq(ApiResource::getUrlPattern, urlPattern)
                        .ne(
                                excludeResourceId != null,
                                ApiResource::getId,
                                excludeResourceId
                        )
        );

        if (count > 0) {
            throw new BizException(
                    40900,
                    "相同 HTTP 方法和 URL 规则的接口资源已存在"
            );
        }
    }

    private String normalizeHttpMethod(String httpMethod) {
        if (httpMethod == null || httpMethod.trim().isEmpty()) {
            throw new BizException(40000, "HTTP 方法不能为空");
        }

        String normalizedMethod = httpMethod
                .trim()
                .toUpperCase(Locale.ROOT);

        if (!SUPPORTED_HTTP_METHODS.contains(normalizedMethod)) {
            throw new BizException(
                    40000,
                    "HTTP 方法只支持 GET、POST、PUT、PATCH、DELETE、HEAD、OPTIONS"
            );
        }

        return normalizedMethod;
    }

    private String normalizeUrlPattern(String urlPattern) {
        if (urlPattern == null || urlPattern.trim().isEmpty()) {
            throw new BizException(40000, "URL 规则不能为空");
        }

        String normalizedUrl = urlPattern.trim();

        if (normalizedUrl.length() > 500) {
            throw new BizException(40000, "URL 规则不能超过 500 个字符");
        }

        if (!normalizedUrl.startsWith("/")) {
            throw new BizException(40000, "URL 规则必须以 / 开头");
        }

        if (normalizedUrl.chars().anyMatch(Character::isWhitespace)) {
            throw new BizException(40000, "URL 规则不能包含空白字符");
        }

        return normalizedUrl;
    }

    private ApiResource getResourceOrThrow(Long resourceId) {
        validateResourceId(resourceId);

        ApiResource resource = apiResourceMapper.selectById(resourceId);

        if (resource == null) {
            throw new BizException(40400, "接口资源不存在");
        }

        return resource;
    }

    private AdminResourceDTO toDTO(ApiResource resource) {
        AdminResourceDTO dto = new AdminResourceDTO();
        dto.setId(resource.getId());
        dto.setResourceName(resource.getResourceName());
        dto.setUrlPattern(resource.getUrlPattern());
        dto.setHttpMethod(resource.getHttpMethod());
        dto.setParentId(resource.getParentId());
        dto.setIsAnonymous(resource.getIsAnonymous());
        dto.setStatus(resource.getStatus());
        dto.setSortOrder(resource.getSortOrder());
        dto.setCreatedAt(resource.getCreatedAt());
        dto.setUpdatedAt(resource.getUpdatedAt());
        dto.setChildren(new ArrayList<>());
        return dto;
    }

    private String normalizeRequiredText(
            String value,
            String emptyMessage,
            int maxLength
    ) {
        if (value == null || value.trim().isEmpty()) {
            throw new BizException(40000, emptyMessage);
        }

        String normalizedValue = value.trim();

        if (normalizedValue.length() > maxLength) {
            throw new BizException(40000, emptyMessage);
        }

        return normalizedValue;
    }

    private Integer validateBinaryStatus(
            Integer value,
            String message
    ) {
        if (value == null || (value != 0 && value != 1)) {
            throw new BizException(40000, message);
        }

        return value;
    }

    private void validateResourceId(Long resourceId) {
        if (resourceId == null || resourceId < 1) {
            throw new BizException(40000, "接口资源 ID 不正确");
        }
    }

    private record NormalizedResource(
            String resourceName,
            String urlPattern,
            String httpMethod,
            Long parentId,
            Integer isAnonymous,
            Integer status,
            Integer sortOrder
    ) {
    }
}
