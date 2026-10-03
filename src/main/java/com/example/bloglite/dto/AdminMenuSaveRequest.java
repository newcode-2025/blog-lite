package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 新增或编辑菜单请求
 */
@Data
public class AdminMenuSaveRequest {

    @NotBlank(message = "菜单名称不能为空")
    @Size(max = 100, message = "菜单名称不能超过 100 个字符")
    private String name;

    @Size(max = 255, message = "路由地址不能超过 255 个字符")
    private String path;

    @Size(max = 255, message = "组件标识不能超过 255 个字符")
    private String component;

    @Size(max = 100, message = "菜单图标不能超过 100 个字符")
    private String icon;

    @NotNull(message = "菜单排序值不能为空")
    @Min(value = 0, message = "菜单排序值不能小于 0")
    private Integer sortOrder;

    @Min(value = 1, message = "父菜单 ID 必须大于 0")
    private Long parentId;

    @NotNull(message = "菜单隐藏状态不能为空")
    @Min(value = 0, message = "菜单隐藏状态只能是 0 或 1")
    @Max(value = 1, message = "菜单隐藏状态只能是 0 或 1")
    private Integer isHidden;

    @NotNull(message = "菜单状态不能为空")
    @Min(value = 0, message = "菜单状态只能是 0 或 1")
    @Max(value = 1, message = "菜单状态只能是 0 或 1")
    private Integer status;
}
