package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.SiteConfigDTO;
import com.example.bloglite.dto.SiteOverviewDTO;
import com.example.bloglite.service.SiteService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 公开站点信息接口
 */
@RestController
@RequestMapping("/api/v1/site")
public class SiteController {

    private final SiteService siteService;

    public SiteController(SiteService siteService) {
        this.siteService = siteService;
    }

    /**
     * 查询公开网站配置
     */
    @GetMapping("/config")
    public ApiResponse<SiteConfigDTO> getConfig() {
        return ApiResponse.success(siteService.getConfig());
    }

    /**
     * 查询站点概览
     */
    @GetMapping("/overview")
    public ApiResponse<SiteOverviewDTO> getOverview() {
        return ApiResponse.success(siteService.getOverview());
    }
}