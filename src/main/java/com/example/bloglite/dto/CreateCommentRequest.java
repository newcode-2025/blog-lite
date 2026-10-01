package com.example.bloglite.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 发表顶级评论请求
 */
@Data
public class CreateCommentRequest {

    /**
     * 评论内容
     */
    @NotBlank(message = "评论内容不能为空")
    @Size(
            max = 1000,
            message = "评论内容不能超过 1000 个字符"
    )
    private String content;
}