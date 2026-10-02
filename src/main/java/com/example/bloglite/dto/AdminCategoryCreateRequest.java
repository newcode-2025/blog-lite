package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 后台新建分类请求
 */
@Data
public class AdminCategoryCreateRequest {

    @NotBlank(message = "分类名称不能为空")
    @Size(
            max = 50,
            message = "分类名称不能超过 50 个字符"
    )
    private String name;

    @Min(value = -9999, message = "排序值不能小于 -9999")
    @Max(value = 9999, message = "排序值不能大于 9999")
    private Integer sortOrder;
}