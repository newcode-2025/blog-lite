package com.example.bloglite.controller;

import com.example.bloglite.annotation.OperationLogRecord;
import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.SiteConfigDTO;
import com.example.bloglite.dto.SiteConfigUpdateRequest;
import com.example.bloglite.service.SiteService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 后台网站配置接口
 */
@RestController
@RequestMapping("/api/v1/admin/site/config")
@PreAuthorize("hasRole('ADMIN')")
public class AdminSiteConfigController {

    private final SiteService siteService;

    public AdminSiteConfigController(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * 管理员查询网站配置
     */
    @GetMapping
    public ApiResponse<SiteConfigDTO> getConfig() {
        return ApiResponse.success(siteService.getConfig());
    }

    /**
     * 管理员更新网站配置
     */
    @OperationLogRecord(module = "网站配置", action = "更新配置", description = "修改网站基础配置")
    @PutMapping
    public ApiResponse<SiteConfigDTO> updateConfig(
            @Valid @RequestBody SiteConfigUpdateRequest request
    ) {
        return ApiResponse.success(siteService.updateConfig(request));
    }
}