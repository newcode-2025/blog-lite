package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 批量审核评论请求
 */
@Data
public class BatchReviewCommentsRequest {

    @NotEmpty(message = "评论 ID 列表不能为空")
    @Size(max = 100, message = "一次最多审核 100 条评论")
    private List<
            @Positive(message = "评论 ID 必须大于 0")
                    Long
            > commentIds;

    /**
     * 1 通过，2 拒绝
     */
    @NotNull(message = "审核状态不能为空")
    @Min(value = 1, message = "审核状态只能是 1 或 2")
    @Max(value = 2, message = "审核状态只能是 1 或 2")
    private Integer status;
}