package com.example.bloglite.service;

import com.example.bloglite.dto.AdminStatsOverviewDTO;
import com.example.bloglite.dto.VisitTrendDTO;

import java.util.List;

/**
 * 后台网站统计查询服务
 */
public interface SiteStatsService {

    /**
     * 查询后台统计总览
     */
    AdminStatsOverviewDTO getOverview();

    /**
     * 查询最近若干天的访问趋势
     */
    List<VisitTrendDTO> getVisitTrend(int days);
}