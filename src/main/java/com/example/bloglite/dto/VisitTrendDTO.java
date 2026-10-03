package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 每日访问趋势
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VisitTrendDTO {

    /**
     * 日期，格式 yyyy-MM-dd
     */
    private String date;

    /**
     * 页面访问量
     */
    private Long pvCount;

    /**
     * 独立访客数
     */
    private Long uvCount;
}