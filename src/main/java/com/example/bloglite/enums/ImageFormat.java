package com.example.bloglite.enums;

import java.util.Arrays;
import java.util.Locale;

/**
 * 阶段六支持的图片格式
 */
public enum ImageFormat {

    JPEG("image/jpeg", "jpg", "jpg", "jpeg"),

    PNG("image/png", "png", "png"),

    GIF("image/gif", "gif", "gif");

    private final String contentType;

    private final String storedExtension;

    private final String[] allowedExtensions;

    ImageFormat(
            String contentType,
            String storedExtension,
            String... allowedExtensions
    ) {
        this.contentType = contentType;
        this.storedExtension = storedExtension;
        this.allowedExtensions = allowedExtensions;
    }

    public String getContentType() {
        return contentType;
    }

    public String getStoredExtension() {
        return storedExtension;
    }

    /**
     * 判断原始扩展名是否与当前图片格式一致
     */
    public boolean supportsExtension(String extension) {
        if (extension == null) {
            return false;
        }

        String normalizedExtension = extension
                .trim()
                .toLowerCase(Locale.ROOT)
                .replaceFirst("^\\.", "");

        return Arrays.stream(allowedExtensions)
                .anyMatch(normalizedExtension::equals);
    }
}
