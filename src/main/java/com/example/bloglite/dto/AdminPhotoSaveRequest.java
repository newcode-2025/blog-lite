package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 后台新建或编辑相册图片请求
 */
@Data
public class AdminPhotoSaveRequest {

    @Size(max = 100, message = "图片名称不能超过 100 个字符")
    private String name;

    @Size(max = 500, message = "图片描述不能超过 500 个字符")
    private String description;

    @NotBlank(message = "图片地址不能为空")
    @Size(max = 500, message = "图片地址不能超过 500 个字符")
    private String photoUrl;

    @Min(value = -9999, message = "排序值不能小于 -9999")
    @Max(value = 9999, message = "排序值不能大于 9999")
    private Integer sortOrder;
}