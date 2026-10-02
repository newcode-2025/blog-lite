package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 后台新建或编辑相册请求
 */
@Data
public class AdminPhotoAlbumSaveRequest {

    @NotBlank(message = "相册名称不能为空")
    @Size(max = 100, message = "相册名称不能超过 100 个字符")
    private String name;

    @Size(max = 500, message = "相册描述不能超过 500 个字符")
    private String description;

    @Size(max = 500, message = "封面地址不能超过 500 个字符")
    private String coverUrl;

    @Min(value = -9999, message = "排序值不能小于 -9999")
    @Max(value = 9999, message = "排序值不能大于 9999")
    private Integer sortOrder;

    /**
     * 1 公开，2 私密
     */
    @Min(value = 1, message = "相册状态只能是 1 或 2")
    @Max(value = 2, message = "相册状态只能是 1 或 2")
    private Integer status;
}