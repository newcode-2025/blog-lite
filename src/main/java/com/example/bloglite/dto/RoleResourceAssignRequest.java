package com.example.bloglite.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.List;

/**
 * 替换角色接口资源请求
 */
@Data
public class RoleResourceAssignRequest {

    @NotNull(message = "接口资源 ID 列表不能为空")
    private List<Long> resourceIds;
}
