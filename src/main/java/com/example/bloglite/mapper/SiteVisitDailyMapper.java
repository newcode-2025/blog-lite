package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.SiteVisitDaily;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

/**
 * 网站每日访问汇总数据访问接口
 */
@Mapper
public interface SiteVisitDailyMapper extends BaseMapper<SiteVisitDaily> {

    /**
     * 创建当日汇总并将 PV 加 1
     */
    @Insert("""
            INSERT INTO site_visit_daily (
                stat_date,
                pv_count,
                uv_count
            )
            VALUES (
                #{statDate},
                1,
                0
            )
            ON DUPLICATE KEY UPDATE
                pv_count = pv_count + 1
            """)
    int insertOrIncrementPv(@Param("statDate") LocalDate statDate);

    /**
     * 当日首次发现访客时将 UV 加 1
     */
    @Insert("""
            UPDATE site_visit_daily
            SET uv_count = uv_count + 1
            WHERE stat_date = #{statDate}
            """)
    int incrementUv(@Param("statDate") LocalDate statDate);
}