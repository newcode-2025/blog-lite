package com.example.bloglite.enums;

import lombok.Getter;

/**
 * 媒体上传业务场景
 */
@Getter
public enum MediaScene {

    /**
     * 文章封面和正文图片
     */
    ARTICLE("article", "文章"),

    /**
     * 说说图片
     */
    TALK("talk", "说说"),

    /**
     * 相册封面和图片
     */
    ALBUM("album", "相册"),

    /**
     * 友链头像
     */
    FRIEND_LINK("friend-link", "友链"),

    /**
     * 用户头像
     */
    AVATAR("avatar", "头像");

    private final String code;

    private final String description;

    MediaScene(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 根据接口传入的场景编码解析枚举
     */
    public static MediaScene fromCode(String code) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("上传场景不能为空");
        }

        String normalizedCode = code.trim().toLowerCase();

        for (MediaScene scene : values()) {
            if (scene.code.equals(normalizedCode)) {
                return scene;
            }
        }

        throw new IllegalArgumentException("不支持的上传场景：" + code);
    }
}
