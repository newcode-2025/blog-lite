package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.dto.AdminStatsOverviewDTO;
import com.example.bloglite.dto.ArticleViewRankDTO;
import com.example.bloglite.dto.VisitTrendDTO;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.entity.SiteVisitDaily;
import com.example.bloglite.enums.ArticleCommentStatus;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ArticleCommentMapper;
import com.example.bloglite.mapper.ArticleMapper;
import com.example.bloglite.mapper.SiteVisitDailyMapper;
import com.example.bloglite.mapper.UserAccountMapper;
import com.example.bloglite.service.SiteStatsService;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 后台网站统计查询服务实现
 */
@Service
public class SiteStatsServiceImpl implements SiteStatsService {

    private static final int MAX_TREND_DAYS = 365;

    private final SiteVisitDailyMapper siteVisitDailyMapper;
    private final ArticleMapper articleMapper;
    private final UserAccountMapper userAccountMapper;
    private final ArticleCommentMapper articleCommentMapper;

    public SiteStatsServiceImpl(
            SiteVisitDailyMapper siteVisitDailyMapper,
            ArticleMapper articleMapper,
            UserAccountMapper userAccountMapper,
            ArticleCommentMapper articleCommentMapper
    ) {
        this.siteVisitDailyMapper = siteVisitDailyMapper;
        this.articleMapper = articleMapper;
        this.userAccountMapper = userAccountMapper;
        this.articleCommentMapper = articleCommentMapper;
    }

    @Override
    public AdminStatsOverviewDTO getOverview() {
        List<SiteVisitDaily> dailyRecords =
                siteVisitDailyMapper.selectList(null);

        long sitePv = 0L;
        long siteUv = 0L;
        long todayPv = 0L;
        long todayUv = 0L;
        LocalDate today = LocalDate.now();

        for (SiteVisitDaily record : dailyRecords) {
            long pv = valueOrZero(record.getPvCount());
            long uv = valueOrZero(record.getUvCount());
            sitePv += pv;
            siteUv += uv;

            if (today.equals(record.getStatDate())) {
                todayPv = pv;
                todayUv = uv;
            }
        }

        long articleCount = articleMapper.countPublishedArticles();
        long userCount = userAccountMapper.selectCount(null);
        long commentCount = articleCommentMapper.selectCount(
                Wrappers.<ArticleComment>lambdaQuery()
                        .eq(ArticleComment::getIsDeleted, 0)
        );
        long pendingCommentCount = articleCommentMapper.selectCount(
                Wrappers.<ArticleComment>lambdaQuery()
                        .eq(
                                ArticleComment::getStatus,
                                ArticleCommentStatus.PENDING.getCode()
                        )
                        .eq(ArticleComment::getIsDeleted, 0)
        );
        Long articleViewCount = articleMapper.sumPublishedArticleViews();
        List<ArticleViewRankDTO> topArticles =
                articleMapper.selectTopViewedArticles(10);

        return new AdminStatsOverviewDTO(
                sitePv,
                siteUv,
                todayPv,
                todayUv,
                articleCount,
                userCount,
                commentCount,
                pendingCommentCount,
                articleViewCount == null ? 0L : articleViewCount,
                topArticles
        );
    }

    @Override
    public List<VisitTrendDTO> getVisitTrend(int days) {
        if (days < 1 || days > MAX_TREND_DAYS) {
            throw new BizException(
                    40000,
                    "统计天数必须在 1 到 " + MAX_TREND_DAYS + " 之间"
            );
        }

        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1L);

        List<SiteVisitDaily> records = siteVisitDailyMapper.selectList(
                Wrappers.<SiteVisitDaily>lambdaQuery()
                        .between(
                                SiteVisitDaily::getStatDate,
                                startDate,
                                today
                        )
                        .orderByAsc(SiteVisitDaily::getStatDate)
        );

        Map<LocalDate, SiteVisitDaily> recordMap = new HashMap<>();
        for (SiteVisitDaily record : records) {
            recordMap.put(record.getStatDate(), record);
        }

        return java.util.stream.IntStream.range(0, days)
                .mapToObj(offset -> {
                    LocalDate date = startDate.plusDays(offset);
                    SiteVisitDaily record = recordMap.get(date);

                    return new VisitTrendDTO(
                            date.toString(),
                            record == null
                                    ? 0L
                                    : valueOrZero(record.getPvCount()),
                            record == null
                                    ? 0L
                                    : valueOrZero(record.getUvCount())
                    );
                })
                .toList();
    }

    private long valueOrZero(Long value) {
        return value == null ? 0L : value;
    }
}