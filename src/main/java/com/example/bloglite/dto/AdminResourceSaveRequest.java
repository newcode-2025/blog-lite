package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增或编辑接口资源请求
 */
@Data
public class AdminResourceSaveRequest {

    @NotBlank(message = "资源名称不能为空")
    @Size(max = 100, message = "资源名称不能超过 100 个字符")
    private String resourceName;

    @Size(max = 500, message = "URL 规则不能超过 500 个字符")
    private String urlPattern;

    @Size(max = 16, message = "HTTP 方法不能超过 16 个字符")
    private String httpMethod;

    @Min(value = 1, message = "父资源 ID 必须大于 0")
    private Long parentId;

    @NotNull(message = "匿名访问状态不能为空")
    @Min(value = 0, message = "匿名访问状态只能是 0 或 1")
    @Max(value = 1, message = "匿名访问状态只能是 0 或 1")
    private Integer isAnonymous;

    @NotNull(message = "资源状态不能为空")
    @Min(value = 0, message = "资源状态只能是 0 或 1")
    @Max(value = 1, message = "资源状态只能是 0 或 1")
    private Integer status;

    @NotNull(message = "资源排序值不能为空")
    @Min(value = 0, message = "资源排序值不能小于 0")
    private Integer sortOrder;
}
