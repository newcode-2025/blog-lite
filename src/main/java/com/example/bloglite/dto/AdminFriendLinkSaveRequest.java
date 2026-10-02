package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 后台新建或编辑友链请求
 */
@Data
public class AdminFriendLinkSaveRequest {

    @NotBlank(message = "友链名称不能为空")
    @Size(max = 100, message = "友链名称不能超过 100 个字符")
    private String name;

    @Size(max = 500, message = "头像地址不能超过 500 个字符")
    private String avatarUrl;

    @NotBlank(message = "友链地址不能为空")
    @Size(max = 500, message = "友链地址不能超过 500 个字符")
    private String address;

    @Size(max = 500, message = "友链介绍不能超过 500 个字符")
    private String description;

    @Min(value = -9999, message = "排序值不能小于 -9999")
    @Max(value = 9999, message = "排序值不能大于 9999")
    private Integer sortOrder;

    @Min(value = 0, message = "友链状态只能是 0 或 1")
    @Max(value = 1, message = "友链状态只能是 0 或 1")
    private Integer status;
}