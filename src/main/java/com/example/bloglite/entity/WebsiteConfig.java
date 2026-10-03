package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 网站配置实体
 */
@Data
@TableName("website_config")
public class WebsiteConfig {

    /**
     * 配置 ID，固定使用第 1 行保存站点配置
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 网站配置 JSON
     */
    private String configJson;

    /**
     * 乐观锁版本号，避免后台并发覆盖
     */
    private Integer version;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
