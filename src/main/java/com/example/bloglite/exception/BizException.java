package com.example.bloglite.exception;

/**
 * 业务异常
 * 用于表示可以预期、可以返回给客户端的错误
 */
public class BizException extends RuntimeException {

    /**
     * 业务错误码
     */
    private final int code;

    public BizException(int code, String message) {
        super(message);
        this.code = code;
    }

    public BizException(int code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}