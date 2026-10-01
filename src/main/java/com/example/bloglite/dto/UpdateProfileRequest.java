package com.example.bloglite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 修改当前用户资料请求
 * 只允许修改昵称、邮箱和头像地址
 */
@Data
public class UpdateProfileRequest {

    /**
     * 用户昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 50, message = "昵称不能超过 50 个字符")
    private String nickname;

    /**
     * 邮箱
     * 传空字符串或 null 表示清空邮箱
     */
    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过 100 个字符")
    private String email;

    /**
     * 头像地址
     * 传空字符串或 null 表示清空头像
     */
    @Size(max = 500, message = "头像地址不能超过 500 个字符")
    private String avatarUrl;
}