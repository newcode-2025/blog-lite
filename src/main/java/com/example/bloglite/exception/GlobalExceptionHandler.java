package com.example.bloglite.exception;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.service.ExceptionLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;

/**
 * 全局异常处理器
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private final ExceptionLogService exceptionLogService;

    public GlobalExceptionHandler(ExceptionLogService exceptionLogService) {
        this.exceptionLogService = exceptionLogService;
    }

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
     * 处理请求参数类型错误
     * 例如：/articles/abc 或 ?current=abc
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ApiResponse<Void> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException exception
    ) {
        return ApiResponse.error(
                40000,
                "参数 " + exception.getName() + " 类型不正确"
        );
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
     * 处理已登录但没有访问权限的请求
     */
    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler(AccessDeniedException.class)
    public ApiResponse<Void> handleAccessDeniedException(
            AccessDeniedException exception
    ) {
        return ApiResponse.error(40300, "无权访问");
    }

    /**
     * 处理明确抛出的业务异常
     */
    @ExceptionHandler(BizException.class)
    public ApiResponse<Void> handleBizException(
            HttpServletRequest request,
            BizException exception
    ) {
        if (exception.getCode() >= 50000) {
            recordSafely(request, exception);
        }

        return ApiResponse.error(
                exception.getCode(),
                exception.getMessage()
        );
    }

    /**
     * 处理数据库唯一索引冲突
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
     * 处理 Multipart 文件大小超过 Spring 配置上限
     */
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ApiResponse<Void> handleMaxUploadSizeExceededException(
            MaxUploadSizeExceededException exception
    ) {
        return ApiResponse.error(41300, "上传文件超过大小限制");
    }

    /**
     * 处理其他 Multipart 请求异常
     */
    @ExceptionHandler(MultipartException.class)
    public ApiResponse<Void> handleMultipartException(
            MultipartException exception
    ) {
        return ApiResponse.error(40000, "上传请求格式不正确");
    }

    /**
     * 处理未预料到的系统异常
     */
    @ExceptionHandler(Exception.class)
    public ApiResponse<Void> handleException(
            HttpServletRequest request,
            Exception exception
    ) {
        log.error("系统未处理异常", exception);
        recordSafely(request, exception);
        return ApiResponse.error(50000, "系统繁忙，请稍后重试");
    }

    /**
     * 异常日志写入失败时只记录应用日志，不覆盖原始异常响应
     */
    private void recordSafely(
            HttpServletRequest request,
            Throwable throwable
    ) {
        try {
            exceptionLogService.recordException(request, throwable);
        } catch (RuntimeException logException) {
            log.error("异常日志记录失败", logException);
        }
    }
}