package com.example.bloglite.exception;

import com.example.bloglite.comment.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 * 统一处理 Controller 抛出的参数异常、业务异常和系统异常
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 处理 @Valid 请求体参数校验失败
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ApiResponse<Void> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException exception
    ) {
        String message = exception.getBindingResult()
                .getAllErrors()
                .stream()
                .findFirst()
                .map(error -> error.getDefaultMessage())
                .orElse("请求参数不正确");

        return ApiResponse.error(40000, message);
    }

    /**
     * 处理 JSON 请求体格式错误
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ApiResponse<Void> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException exception
    ) {
        return ApiResponse.error(40000, "请求体格式不正确，请检查 JSON 格式");
    }

    /**
     * 处理明确抛出的业务异常
     */
    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBizException(BizException exception) {
        return ApiResponse.error(
                exception.getCode(),
                exception.getMessage()
        );
    }

    /**
     * 处理数据库唯一索引冲突
     * 用于 Service 查询之后、插入之前发生并发重复写入的兜底情况
     */
    @ExceptionHandler(DuplicateKeyException.class)
    public ApiResponse<Void> handleDuplicateKeyException(
            DuplicateKeyException exception
    ) {
        return ApiResponse.error(40900, "用户名或邮箱已存在");
    }

    /**
     * 处理普通参数异常
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ApiResponse<Void> handleIllegalArgumentException(
            IllegalArgumentException exception
    ) {
        return ApiResponse.error(40000, exception.getMessage());
    }

    /**
     * 处理未预料到的系统异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(Exception exception) {
        // 系统异常需要记录完整堆栈，方便定位问题
        log.error("系统未处理异常", exception);

        // 不把数据库、文件路径等内部信息返回给客户端
        return ApiResponse.error(50000, "系统繁忙，请稍后重试");
    }
}