package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 评论审核请求
 */
@Data
public class ReviewCommentRequest {

    /**
     * 审核状态
     * 1：通过
     * 2：拒绝
     */
    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态只能是 1 或 2")
    @Max(value = 2, message = "审核状态只能是 1 或 2")
    private Integer status;
}