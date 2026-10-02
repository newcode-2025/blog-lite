package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 后台编辑分类请求
 */
@Data
public class AdminCategoryUpdateRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(
            max = 50,
            message = "分类名称不能超过 50 个字符"
    )
    private String name;

    @NotNull(message = "排序值不能为空")
    @Min(value = -9999, message = "排序值不能小于 -9999")
    @Max(value = 9999, message = "排序值不能大于 9999")
    private Integer sortOrder;
}