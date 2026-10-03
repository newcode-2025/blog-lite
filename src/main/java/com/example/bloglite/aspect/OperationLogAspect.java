package com.example.bloglite.aspect;

import com.example.bloglite.annotation.OperationLogRecord;
import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.OperationLog;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.service.impl.OperationLogPersistenceService;
import com.example.bloglite.util.LogSanitizer;
import com.example.bloglite.util.RequestIpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.AnnotatedElementUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.lang.reflect.Method;
import java.time.LocalDateTime;

/**
 * 操作日志切面
 */
@Aspect
@Component
public class OperationLogAspect {

    private final OperationLogPersistenceService persistenceService;

    public OperationLogAspect(
            OperationLogPersistenceService persistenceService
    ) {
        this.persistenceService = persistenceService;
    }

    @Around("execution(public * com.example.bloglite.controller..*(..))")
    public Object recordOperation(ProceedingJoinPoint joinPoint)
            throws Throwable {
        HttpServletRequest request = currentRequest();
        Method method = ((MethodSignature) joinPoint.getSignature())
                .getMethod();
        OperationLogRecord annotation = AnnotatedElementUtils
                .findMergedAnnotation(method, OperationLogRecord.class);

        if (!shouldRecord(request, annotation)) {
            return joinPoint.proceed();
        }

        long start = System.currentTimeMillis();
        OperationLog operationLog = createBaseLog(joinPoint, request, annotation);
        boolean success = true;
        Integer responseCode = null;
        String errorMessage = null;

        try {
            Object result = joinPoint.proceed();

            if (result instanceof ApiResponse<?> response) {
                responseCode = response.getCode();
                success = Integer.valueOf(20000).equals(responseCode);
                if (!success) {
                    errorMessage = response.getMessage();
                }
            }

            return result;
        } catch (Throwable throwable) {
            success = false;
            responseCode = throwable instanceof BizException bizException
                    ? bizException.getCode()
                    : 50000;
            errorMessage = throwable.getClass().getSimpleName()
                    + ": " + throwable.getMessage();
            throw throwable;
        } finally {
            operationLog.setSuccess(success ? 1 : 0);
            operationLog.setResponseCode(responseCode);
            operationLog.setErrorMessage(truncate(errorMessage, 1000));
            operationLog.setDurationMs(System.currentTimeMillis() - start);
            operationLog.setCreatedAt(LocalDateTime.now());
            persistenceService.save(operationLog);
        }
    }

    private OperationLog createBaseLog(
            ProceedingJoinPoint joinPoint,
            HttpServletRequest request,
            OperationLogRecord annotation
    ) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        OperationLog log = new OperationLog();
        log.setModule(resolveModule(annotation, joinPoint));
        log.setOperationAction(resolveAction(annotation, method));
        log.setDescription(annotation == null ? "" : annotation.description());
        log.setRequestMethod(request.getMethod());
        log.setRequestUri(request.getRequestURI());
        log.setHandlerMethod(
                method.getDeclaringClass().getName() + "." + method.getName()
        );
        log.setRequestParams(LogSanitizer.sanitizeArguments(joinPoint.getArgs()));
        log.setIpAddress(RequestIpUtil.getClientIp(request));
        fillOperator(log);
        return log;
    }

    private boolean shouldRecord(
            HttpServletRequest request,
            OperationLogRecord annotation
    ) {
        if (request == null) {
            return false;
        }

        if (annotation != null) {
            return true;
        }

        String uri = request.getRequestURI();
        String method = request.getMethod();

        if ("GET".equalsIgnoreCase(method)
                || "OPTIONS".equalsIgnoreCase(method)
                || "HEAD".equalsIgnoreCase(method)) {
            return false;
        }

        return uri.startsWith("/api/v1/admin/")
                || uri.startsWith("/api/v1/users/me")
                || uri.startsWith("/api/v1/comments/")
                || uri.matches("/api/v1/articles/\\d+/comments/?");
    }

    private String resolveModule(
            OperationLogRecord annotation,
            ProceedingJoinPoint joinPoint
    ) {
        if (annotation != null && !annotation.module().isBlank()) {
            return annotation.module();
        }

        String simpleName = joinPoint.getTarget()
                .getClass()
                .getSimpleName();
        return simpleName.endsWith("Controller")
                ? simpleName.substring(0, simpleName.length() - 10)
                : simpleName;
    }

    private String resolveAction(
            OperationLogRecord annotation,
            Method method
    ) {
        return annotation != null && !annotation.action().isBlank()
                ? annotation.action()
                : method.getName();
    }

    private void fillOperator(OperationLog operationLog) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserDTO user)) {
            return;
        }

        operationLog.setOperatorId(user.getId());
        operationLog.setOperatorName(user.getNickname());
    }

    private HttpServletRequest currentRequest() {
        if (!(RequestContextHolder.getRequestAttributes()
                instanceof ServletRequestAttributes attributes)) {
            return null;
        }
        return attributes.getRequest();
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }
}