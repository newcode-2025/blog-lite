package com.example.bloglite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * 邮件发送测试请求
 */
@Data
public class MailTestRequest {

    @NotBlank(message = "收件邮箱不能为空")
    @Email(message = "收件邮箱格式不正确")
    @Size(max = 100, message = "收件邮箱不能超过 100 个字符")
    private String to;

    @Size(max = 100, message = "邮件主题不能超过 100 个字符")
    private String subject;
}