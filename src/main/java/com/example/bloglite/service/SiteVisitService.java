package com.example.bloglite.service;

/**
 * 网站访问统计服务
 */
public interface SiteVisitService {

    /**
     * 记录一次公开页面访问
     */
    void reportVisit(String ipAddress, String userAgent);
}