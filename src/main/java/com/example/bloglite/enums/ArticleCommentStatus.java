package com.example.bloglite.enums;

/**
 * 文章评论审核状态
 */
public enum ArticleCommentStatus {

    /**
     * 待审核
     */
    PENDING(0, "待审核"),

    /**
     * 已通过
     */
    APPROVED(1, "已通过"),

    /**
     * 已拒绝
     */
    REJECTED(2, "已拒绝");

    private final int code;

    private final String description;

    ArticleCommentStatus(int code, String description) {
        this.code = code;
        this.description = description;
    }

    public int getCode() {
        return code;
    }

    public String getDescription() {
        return description;
    }

    /**
     * 根据数据库状态码查找枚举
     */
    public static ArticleCommentStatus fromCode(Integer code) {
        if (code == null) {
            throw new IllegalArgumentException("评论状态不能为空");
        }

        for (ArticleCommentStatus status : values()) {
            if (status.code == code) {
                return status;
            }
        }

        throw new IllegalArgumentException("不支持的评论状态：" + code);
    }
}