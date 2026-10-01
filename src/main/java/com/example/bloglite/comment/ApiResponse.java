package com.example.bloglite.comment;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * 统一接口响应
 */
@Data
@AllArgsConstructor
public class ApiResponse<T> {

    /**
     * 业务状态码：
     * 20000：成功
     * 40000：参数错误
     * 40900：数据冲突，例如用户名或邮箱重复
     * 50000：系统错误
     */
    private int code;

    private String message;

    private T data;

    /**
     * 成功响应
     */
    public static <T> ApiResponse<T> success(T data) {
        return new ApiResponse<>(20000, "操作成功", data);
    }

    /**
     * 默认错误响应，错误码为 50000
     */
    public static <T> ApiResponse<T> error(String message) {
        return error(50000, message);
    }

    /**
     * 自定义错误码的响应
     */
    public static <T> ApiResponse<T> error(int code, String message) {
        return new ApiResponse<>(code, message, null);
    }
}
