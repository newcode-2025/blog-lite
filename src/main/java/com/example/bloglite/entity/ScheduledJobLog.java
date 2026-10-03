package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 定时任务执行日志实体
 */
@Data
@TableName("scheduled_job_log")
public class ScheduledJobLog {

    /**
     * 日志 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 任务 ID
     */
    private Long jobId;

    /**
     * 白名单任务键
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
     * 触发方式：SCHEDULE 或 MANUAL
     */
    private String triggerType;

    /**
     * 执行状态：0 失败，1 成功
     */
    private Integer status;

    /**
     * 执行消息
     */
    private String jobMessage;

    /**
     * 异常信息
     */
    private String exceptionInfo;

    /**
     * 开始时间
     */
    private LocalDateTime startTime;

    /**
     * 结束时间
     */
    private LocalDateTime endTime;

    /**
     * 执行耗时，单位毫秒
     */
    private Long durationMs;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;
}
