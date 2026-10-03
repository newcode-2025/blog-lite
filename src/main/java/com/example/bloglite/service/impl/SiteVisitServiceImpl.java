package com.example.bloglite.service.impl;

import com.example.bloglite.config.StatsProperties;
import com.example.bloglite.mapper.SiteVisitDailyMapper;
import com.example.bloglite.mapper.SiteVisitorDailyMapper;
import com.example.bloglite.service.SiteVisitService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HexFormat;

/**
 * 网站访问统计服务实现
 */
@Service
public class SiteVisitServiceImpl implements SiteVisitService {

    private static final int MAX_USER_AGENT_LENGTH = 500;

    private final SiteVisitDailyMapper siteVisitDailyMapper;
    private final SiteVisitorDailyMapper siteVisitorDailyMapper;
    private final StatsProperties statsProperties;

    public SiteVisitServiceImpl(
            SiteVisitDailyMapper siteVisitDailyMapper,
            SiteVisitorDailyMapper siteVisitorDailyMapper,
            StatsProperties statsProperties
    ) {
        this.siteVisitDailyMapper = siteVisitDailyMapper;
        this.siteVisitorDailyMapper = siteVisitorDailyMapper;
        this.statsProperties = statsProperties;
    }

    @Override
    @Transactional
    public void reportVisit(String ipAddress, String userAgent) {
        LocalDate statDate = LocalDate.now();
        String normalizedIp = normalize(ipAddress);
        String normalizedUserAgent = normalizeUserAgent(userAgent);
        String visitorHash = calculateVisitorHash(
                statDate,
                normalizedIp,
                normalizedUserAgent
        );

        siteVisitDailyMapper.insertOrIncrementPv(statDate);

        int inserted = siteVisitorDailyMapper.insertIgnoreVisitor(
                statDate,
                visitorHash,
                LocalDateTime.now()
        );

        if (inserted == 1) {
            siteVisitDailyMapper.incrementUv(statDate);
        }
    }

    private String calculateVisitorHash(
            LocalDate statDate,
            String ipAddress,
            String userAgent
    ) {
        String source = statDate
                + "|" + ipAddress
                + "|" + userAgent
                + "|" + statsProperties.getVisitorHashSalt();

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(
                    source.getBytes(StandardCharsets.UTF_8)
            );
            return HexFormat.of().formatHex(hash);
        } catch (NoSuchAlgorithmException exception) {
            throw new IllegalStateException("当前运行环境不支持 SHA-256", exception);
        }
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim();
    }

    private String normalizeUserAgent(String userAgent) {
        String normalized = normalize(userAgent);

        if (normalized.length() <= MAX_USER_AGENT_LENGTH) {
            return normalized;
        }

        return normalized.substring(0, MAX_USER_AGENT_LENGTH);
    }
}