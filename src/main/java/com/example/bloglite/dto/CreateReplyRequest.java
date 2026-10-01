package com.example.bloglite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发表评论回复请求
 */
@Data
public class CreateReplyRequest {

    /**
     * 回复内容
     */
    @NotBlank(message = "回复内容不能为空")
    @Size(
            max = 1000,
            message = "回复内容不能超过 1000 个字符"
    )
    private String content;
}