package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 网站每日访客去重实体
 */
@Data
@TableName("site_visitor_daily")
public class SiteVisitorDaily {

    /**
     * 访客记录 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 加盐后的访客哈希，不保存原始 IP
     */
    private String visitorHash;

    /**
     * 当日首次访问时间
     */
    private LocalDateTime firstSeenAt;
}
