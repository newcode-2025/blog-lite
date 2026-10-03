package com.example.bloglite.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 替换角色菜单请求
 */
@Data
public class RoleMenuAssignRequest {

    @NotNull(message = "菜单 ID 列表不能为空")
    private List<Long> menuIds;
}
