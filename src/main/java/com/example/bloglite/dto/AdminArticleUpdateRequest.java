package com.example.bloglite.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/**
 * 后台更新文章请求
 */
@Data
public class AdminArticleUpdateRequest {

    @NotBlank(message = "文章标题不能为空")
    @Size(
            max = 200,
            message = "文章标题不能超过 200 个字符"
    )
    private String title;

    @Size(
            max = 500,
            message = "文章摘要不能超过 500 个字符"
    )
    private String summary;

    @NotBlank(message = "文章正文不能为空")
    private String content;

    @Size(
            max = 500,
            message = "封面地址不能超过 500 个字符"
    )
    private String coverUrl;

    @Positive(message = "分类 ID 必须大于 0")
    private Long categoryId;

    @Size(
            max = 20,
            message = "一篇文章最多关联 20 个标签"
    )
    private List<
            @Positive(message = "标签 ID 必须大于 0")
                    Long
            > tagIds;

    /**
     * 1 公开，2 密码访问，3 草稿
     */
    @NotNull(message = "文章状态不能为空")
    @Min(value = 1, message = "文章状态只能是 1、2 或 3")
    @Max(value = 3, message = "文章状态只能是 1、2 或 3")
    private Integer status;

    /**
     * 仅 status = 2 时使用。
     * 不传或传空表示保留原密码。
     */
    @Size(
            max = 100,
            message = "文章访问密码不能超过 100 个字符"
    )
    private String accessPassword;
}