package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * 后台修改文章置顶和推荐请求
 */
@Data
public class AdminArticleFlagsUpdateRequest {

    @NotNull(message = "置顶状态不能为空")
    @Min(value = 0, message = "置顶状态只能是 0 或 1")
    @Max(value = 1, message = "置顶状态只能是 0 或 1")
    private Integer isTop;

    @NotNull(message = "推荐状态不能为空")
    @Min(value = 0, message = "推荐状态只能是 0 或 1")
    @Max(value = 1, message = "推荐状态只能是 0 或 1")
    private Integer isFeatured;
}
