package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.service.SiteVisitService;
import com.example.bloglite.util.RequestIpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 网站访问上报接口
 */
@RestController
@RequestMapping("/api/v1/stats")
public class SiteVisitController {

    private final SiteVisitService siteVisitService;

    public SiteVisitController(SiteVisitService siteVisitService) {
        this.siteVisitService = siteVisitService;
    }

    /**
     * 匿名上报一次公开页面访问
     */
    @PostMapping("/report")
    public ApiResponse<Void> report(HttpServletRequest request) {
        siteVisitService.reportVisit(
                RequestIpUtil.getClientIp(request),
                request.getHeader("User-Agent")
        );
        return ApiResponse.success(null);
    }
}