package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 后台修改文章状态请求
 */
@Data
public class AdminArticleStatusUpdateRequest {

    /**
     * 1 公开，2 密码访问，3 草稿
     */
    @NotNull(message = "文章状态不能为空")
    @Min(value = 1, message = "文章状态只能是 1、2 或 3")
    @Max(value = 3, message = "文章状态只能是 1、2 或 3")
    private Integer status;

    /**
     * 仅切换到 status = 2 且需要修改密码时传递。
     * 不传或传空表示保留原密码。
     */
    @Size(
            max = 100,
            message = "文章访问密码不能超过 100 个字符"
    )
    private String accessPassword;
}