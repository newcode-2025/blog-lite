package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 编辑角色请求
 */
@Data
public class AdminRoleUpdateRequest {

    @NotBlank(message = "角色名称不能为空")
    @Size(max = 50, message = "角色名称不能超过 50 个字符")
    private String roleName;

    @Size(max = 255, message = "角色说明不能超过 255 个字符")
    private String description;

    @NotNull(message = "角色状态不能为空")
    @Min(value = 0, message = "角色状态只能是 0 或 1")
    @Max(value = 1, message = "角色状态只能是 0 或 1")
    private Integer status;
}
