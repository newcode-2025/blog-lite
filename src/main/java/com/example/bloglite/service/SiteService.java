package com.example.bloglite.service;

import com.example.bloglite.dto.SiteConfigDTO;
import com.example.bloglite.dto.SiteConfigUpdateRequest;
import com.example.bloglite.dto.SiteOverviewDTO;

/**
 * 站点信息与网站配置服务
 */
public interface SiteService {

    /**
     * 查询公开网站配置
     */
    SiteConfigDTO getConfig();

    /**
     * 查询公开站点概览
     */
    SiteOverviewDTO getOverview();

    /**
     * 更新网站配置
     */
    SiteConfigDTO updateConfig(SiteConfigUpdateRequest request);
}