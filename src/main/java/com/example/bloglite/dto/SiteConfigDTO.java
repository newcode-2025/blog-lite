package com.example.bloglite.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 网站配置传输对象
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SiteConfigDTO {

    /**
     * 配置版本号，管理员更新时必须回传
     */
    @NotNull(message = "配置版本号不能为空")
    private Integer version;

    @NotBlank(message = "网站名称不能为空")
    @Size(max = 100, message = "网站名称不能超过 100 个字符")
    private String siteName;

    @Size(max = 500, message = "网站描述不能超过 500 个字符")
    private String siteDescription;

    @Size(max = 50, message = "作者名称不能超过 50 个字符")
    private String authorName;

    @Size(max = 500, message = "作者头像地址不能超过 500 个字符")
    private String authorAvatar;

    @Size(max = 1000, message = "作者简介不能超过 1000 个字符")
    private String authorIntro;

    @Size(max = 500, message = "网站 Logo 地址不能超过 500 个字符")
    private String logo;

    @Size(max = 1000, message = "网站公告不能超过 1000 个字符")
    private String notice;

    @Pattern(
            regexp = "^$|^\\d{4}-\\d{2}-\\d{2}$",
            message = "网站创建时间必须使用 yyyy-MM-dd 格式"
    )
    private String websiteCreateTime;

    @Size(max = 100, message = "备案号不能超过 100 个字符")
    private String icpNumber;

    @Size(max = 500, message = "GitHub 地址不能超过 500 个字符")
    private String github;

    @Size(max = 500, message = "Gitee 地址不能超过 500 个字符")
    private String gitee;

    @Size(max = 100, message = "QQ 不能超过 100 个字符")
    private String qq;

    @Size(max = 100, message = "微信不能超过 100 个字符")
    private String weChat;

    @Size(max = 500, message = "微博地址不能超过 500 个字符")
    private String weibo;

    @Size(max = 500, message = "CSDN 地址不能超过 500 个字符")
    private String csdn;

    @Size(max = 500, message = "知乎地址不能超过 500 个字符")
    private String zhihu;

    @Size(max = 500, message = "掘金地址不能超过 500 个字符")
    private String juejin;

    @Size(max = 500, message = "Twitter 地址不能超过 500 个字符")
    private String twitter;

    @Size(max = 500, message = "Stack Overflow 地址不能超过 500 个字符")
    private String stackOverflow;

    @Email(message = "邮箱格式不正确")
    @Size(max = 100, message = "邮箱不能超过 100 个字符")
    private String email;

    @NotNull(message = "评论审核开关不能为空")
    @Min(value = 0, message = "评论审核开关只能为 0 或 1")
    @Max(value = 1, message = "评论审核开关只能为 0 或 1")
    private Integer commentReviewEnabled;

    @NotNull(message = "邮件通知开关不能为空")
    @Min(value = 0, message = "邮件通知开关只能为 0 或 1")
    @Max(value = 1, message = "邮件通知开关只能为 0 或 1")
    private Integer emailNotificationEnabled;
}