package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 公开站点概览
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteOverviewDTO {

    /**
     * 公开文章数量
     */
    private Long articleCount;

    /**
     * 分类数量
     */
    private Long categoryCount;

    /**
     * 标签数量
     */
    private Long tagCount;

    /**
     * 公开说说数量
     */
    private Long talkCount;

    /**
     * 审核通过且未删除的评论数量
     */
    private Long commentCount;

    /**
     * 公开文章累计阅读量
     */
    private Long articleViewCount;

    /**
     * 网站配置
     */
    private SiteConfigDTO siteConfig;
}