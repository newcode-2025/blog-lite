package com.example.bloglite.dto;

import com.example.bloglite.enums.ImageFormat;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 校验通过的图片信息
 */
@Data
@AllArgsConstructor
public class ValidatedImage {

    /**
     * 原始文件名，仅用于响应展示
     */
    private String originalName;

    /**
     * 识别得到的真实图片格式
     */
    private ImageFormat format;

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
}
