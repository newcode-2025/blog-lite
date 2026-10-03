package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务定义实体
 */
@Data
@TableName("scheduled_job")
public class ScheduledJob {

    /**
     * 任务 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 白名单任务键，不能保存任意类名或方法名
     */
    private String taskKey;

    /**
     * 任务名称
     */
    private String jobName;

    /**
     * 任务分组
     */
    private String jobGroup;

    /**
     * Cron 表达式
     */
    private String cronExpression;

    /**
     * 状态：0 暂停，1 运行
     */
    private Integer status;

    /**
     * 是否允许并发：0 否，1 是
     */
    private Integer concurrent;

    /**
     * 错失执行策略：0 默认，1 忽略，2 立即补发，3 不补发
     */
    private Integer misfirePolicy;

    /**
     * 任务说明
     */
    private String description;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
