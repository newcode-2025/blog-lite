package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.SiteVisitorDaily;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 网站每日访客去重数据访问接口
 */
@Mapper
public interface SiteVisitorDailyMapper extends BaseMapper<SiteVisitorDaily> {

    /**
     * 插入当日访客哈希，已存在时不产生重复记录
     */
    @Insert("""
            INSERT IGNORE INTO site_visitor_daily (
                stat_date,
                visitor_hash,
                first_seen_at
            )
            VALUES (
                #{statDate},
                #{visitorHash},
                #{firstSeenAt}
            )
            """)
    int insertIgnoreVisitor(
            @Param("statDate") LocalDate statDate,
            @Param("visitorHash") String visitorHash,
            @Param("firstSeenAt") LocalDateTime firstSeenAt
    );
}