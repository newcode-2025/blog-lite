package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 邮件通知记录实体
 */
@Data
@TableName("notification_log")
public class NotificationLog {

    /**
     * 通知记录 ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知类型，例如 COMMENT_CREATED、COMMENT_APPROVED
     */
    private String notificationType;

    /**
     * 业务类型，例如 ARTICLE_COMMENT
     */
    private String businessType;

    /**
     * 业务 ID
     */
    private Long businessId;

    /**
     * 接收人用户 ID
     */
    private Long recipientUserId;

    /**
     * 接收人邮箱快照
     */
    private String recipientEmail;

    /**
     * 发送状态：0 待发送，1 成功，2 失败
     */
    private Integer status;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 最近一次错误
     */
    private String lastError;

    /**
     * 成功发送时间
     */
    private LocalDateTime sentAt;

    /**
     * 创建时间
     */
    private LocalDateTime createdAt;

    /**
     * 更新时间
     */
    private LocalDateTime updatedAt;
}
