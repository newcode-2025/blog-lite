package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 后台统计总览
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdminStatsOverviewDTO {

    /**
     * 网站累计 PV
     */
    private Long sitePv;

    /**
     * 网站累计 UV
     */
    private Long siteUv;

    /**
     * 今日 PV
     */
    private Long todayPv;

    /**
     * 今日 UV
     */
    private Long todayUv;

    /**
     * 公开文章数量
     */
    private Long articleCount;

    /**
     * 用户数量
     */
    private Long userCount;

    /**
     * 未删除评论数量
     */
    private Long commentCount;

    /**
     * 待审核评论数量
     */
    private Long pendingCommentCount;

    /**
     * 公开文章累计阅读量
     */
    private Long articleViewCount;

    /**
     * 阅读量最高文章
     */
    private List<ArticleViewRankDTO> topArticles;
}