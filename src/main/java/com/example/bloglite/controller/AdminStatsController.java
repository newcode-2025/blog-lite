package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminStatsOverviewDTO;
import com.example.bloglite.dto.VisitTrendDTO;
import com.example.bloglite.service.SiteStatsService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台统计接口
 */
@RestController
@RequestMapping("/api/v1/admin/stats")
@PreAuthorize("hasRole('ADMIN')")
public class AdminStatsController {

    private final SiteStatsService siteStatsService;

    public AdminStatsController(SiteStatsService siteStatsService) {
        this.siteStatsService = siteStatsService;
    }

    /**
     * 查询统计总览
     */
    @GetMapping("/overview")
    public ApiResponse<AdminStatsOverviewDTO> getOverview() {
        return ApiResponse.success(siteStatsService.getOverview());
    }

    /**
     * 查询最近若干天访问趋势
     */
    @GetMapping("/visits")
    public ApiResponse<List<VisitTrendDTO>> getVisitTrend(
            @RequestParam(defaultValue = "30") int days
    ) {
        return ApiResponse.success(siteStatsService.getVisitTrend(days));
    }
}