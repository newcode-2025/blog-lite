package com.example.bloglite.config;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.unit.DataSize;
import org.springframework.validation.annotation.Validated;

/**
 * 媒体上传配置属性
 * 对应 application.yaml 中的 blog.upload 配置
 */
@Data
@Component
@Validated
@ConfigurationProperties(prefix = "blog.upload")
public class UploadProperties {

    /**
     * 本地文件保存根目录
     */
    @NotBlank(message = "本地上传目录不能为空")
    private String localDir;

    /**
     * 对外静态访问路径前缀
     */
    @NotBlank(message = "上传访问前缀不能为空")
    private String publicPath;

    /**
     * 对外访问地址根路径
     */
    @NotBlank(message = "上传访问地址不能为空")
    private String publicBaseUrl;

    /**
     * 单张图片允许的最大字节数
     */
    @NotNull(message = "单张图片大小限制不能为空")
    private DataSize maxFileSize;

    /**
     * 单次批量上传允许的最大图片数量
     */
    @Min(value = 1, message = "批量上传数量限制必须大于 0")
    private int maxBatchCount;

    /**
     * 单张图片允许的最大像素总数
     */
    @Min(value = 1, message = "图片像素限制必须大于 0")
    private long maxImagePixels;
}

