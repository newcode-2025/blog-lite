package com.example.bloglite.dto;

import com.example.bloglite.enums.MediaScene;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 媒体上传结果
 */
@Data
@AllArgsConstructor
public class MediaUploadResultDTO {

    /**
     * 项目内公开访问地址，例如 /uploads/article/2026/10/xxx.png
     */
    private String url;

    /**
     * 原始文件名
     */
    private String originalName;

    /**
     * 实际图片类型
     */
    private String contentType;

    /**
     * 文件字节数
     */
    private long size;

    /**
     * 图片宽度
     */
    private int width;

    /**
     * 图片高度
     */
    private int height;

    /**
     * 上传场景
     */
    private String scene;
}
