package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 后台新建或编辑说说请求
 */
@Data
public class AdminTalkSaveRequest {

    @NotBlank(message = "说说内容不能为空")
    @Size(max = 2000, message = "说说内容不能超过 2000 个字符")
    private String content;

    /**
     * 新建时不传表示空数组；
     * 编辑时不传表示保留原图片；
     * 编辑时传空数组表示清空图片。
     */
    @Size(max = 9, message = "一条说说最多包含 9 张图片")
    private List<
            @NotBlank(message = "图片地址不能为空")
            @Size(max = 500, message = "图片地址不能超过 500 个字符")
                    String
            > images;

    @Min(value = 0, message = "置顶状态只能是 0 或 1")
    @Max(value = 1, message = "置顶状态只能是 0 或 1")
    private Integer isTop;

    /**
     * 1 公开，2 私密
     */
    @Min(value = 1, message = "说说状态只能是 1 或 2")
    @Max(value = 2, message = "说说状态只能是 1 或 2")
    private Integer status;
}