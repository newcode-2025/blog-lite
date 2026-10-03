package com.example.bloglite.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 替换后台用户角色请求
 */
@Data
public class AdminUserRoleUpdateRequest {

    @NotNull(message = "角色 ID 列表不能为空")
    private List<Long> roleIds;
}
