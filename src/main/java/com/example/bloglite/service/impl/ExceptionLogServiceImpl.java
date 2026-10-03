package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bloglite.dto.ExceptionLogDTO;
import com.example.bloglite.dto.ExceptionLogDeleteRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.ExceptionLog;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ExceptionLogMapper;
import com.example.bloglite.service.ExceptionLogService;
import com.example.bloglite.util.LogSanitizer;
import com.example.bloglite.util.RequestIpUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 系统异常日志服务实现
 */
@Service
public class ExceptionLogServiceImpl implements ExceptionLogService {

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;
    private static final int MAX_DELETE_COUNT = 100;
    private static final int MAX_STACK_LENGTH = 10000;
    private static final String BEST_MATCHING_HANDLER =
            "org.springframework.web.servlet.HandlerMapping.bestMatchingHandler";

    private final ExceptionLogMapper exceptionLogMapper;
    private final ExceptionLogPersistenceService persistenceService;

    public ExceptionLogServiceImpl(
            ExceptionLogMapper exceptionLogMapper,
            ExceptionLogPersistenceService persistenceService
    ) {
        this.exceptionLogMapper = exceptionLogMapper;
        this.persistenceService = persistenceService;
    }

    @Override
    public void recordException(
            HttpServletRequest request,
            Throwable throwable
    ) {
        if (throwable == null) {
            return;
        }

        ExceptionLog exceptionLog = new ExceptionLog();
        exceptionLog.setRequestMethod(
                request == null ? "" : request.getMethod()
        );
        exceptionLog.setRequestUri(
                request == null ? "" : request.getRequestURI()
        );
        exceptionLog.setHandlerMethod(resolveHandlerMethod(request));
        exceptionLog.setRequestParams(
                request == null
                        ? ""
                        : LogSanitizer.sanitizeText(request.getQueryString())
        );
        exceptionLog.setExceptionType(throwable.getClass().getName());
        exceptionLog.setExceptionMessage(
                truncate(throwable.getMessage(), 1000)
        );
        exceptionLog.setStackTrace(buildStackTrace(throwable));
        exceptionLog.setIpAddress(
                request == null ? "" : RequestIpUtil.getClientIp(request)
        );
        fillOperator(exceptionLog);
        exceptionLog.setCreatedAt(LocalDateTime.now());
        persistenceService.save(exceptionLog);
    }

    @Override
    public PageResultDTO<ExceptionLogDTO> listLogs(
            String exceptionType,
            String requestUri,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long current,
            long size
    ) {
        if (startTime != null && endTime != null
                && startTime.isAfter(endTime)) {
            throw new BizException(40000, "开始时间不能晚于结束时间");
        }

        long normalizedCurrent = current < 1 ? 1 : current;
        long normalizedSize = size < 1
                ? DEFAULT_SIZE
                : Math.min(size, MAX_SIZE);
        long offset = (normalizedCurrent - 1) * normalizedSize;

        Long count = exceptionLogMapper.selectCount(
                buildWrapper(
                        exceptionType,
                        requestUri,
                        startTime,
                        endTime
                )
        );
        List<ExceptionLogDTO> records = exceptionLogMapper.selectList(
                        buildWrapper(
                                exceptionType,
                                requestUri,
                                startTime,
                                endTime
                        )
                                .orderByDesc(ExceptionLog::getCreatedAt)
                                .orderByDesc(ExceptionLog::getId)
                                .last("LIMIT " + offset + "," + normalizedSize)
                )
                .stream()
                .map(this::toDTO)
                .toList();

        return new PageResultDTO<>(
                records,
                count,
                normalizedCurrent,
                normalizedSize
        );
    }

    @Override
    public void deleteLogs(ExceptionLogDeleteRequest request) {
        if (request == null || request.getIds() == null
                || request.getIds().isEmpty()) {
            throw new BizException(40000, "异常日志 ID 列表不能为空");
        }

        if (request.getIds().size() > MAX_DELETE_COUNT) {
            throw new BizException(40000, "一次最多删除 100 条异常日志");
        }

        Set<Long> uniqueIds = new HashSet<>();

        for (Long id : request.getIds()) {
            if (id == null || id < 1) {
                throw new BizException(40000, "异常日志 ID 必须大于 0");
            }
            if (!uniqueIds.add(id)) {
                throw new BizException(40000, "异常日志 ID 不能重复");
            }
        }

        exceptionLogMapper.deleteByIds(uniqueIds);
    }

    private LambdaQueryWrapper<ExceptionLog> buildWrapper(
            String exceptionType,
            String requestUri,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return new LambdaQueryWrapper<ExceptionLog>()
                .like(StringUtils.hasText(exceptionType),
                        ExceptionLog::getExceptionType,
                        exceptionType == null ? null : exceptionType.trim())
                .like(StringUtils.hasText(requestUri),
                        ExceptionLog::getRequestUri,
                        requestUri == null ? null : requestUri.trim())
                .ge(startTime != null,
                        ExceptionLog::getCreatedAt,
                        startTime)
                .le(endTime != null,
                        ExceptionLog::getCreatedAt,
                        endTime);
    }

    private String resolveHandlerMethod(HttpServletRequest request) {
        if (request == null) {
            return "";
        }

        Object handler = request.getAttribute(BEST_MATCHING_HANDLER);

        if (handler instanceof HandlerMethod handlerMethod) {
            return handlerMethod.getBeanType().getName()
                    + "." + handlerMethod.getMethod().getName();
        }

        return handler == null ? "" : handler.getClass().getName();
    }

    private String buildStackTrace(Throwable throwable) {
        StringWriter stringWriter = new StringWriter();
        throwable.printStackTrace(new PrintWriter(stringWriter));
        return truncate(stringWriter.toString(), MAX_STACK_LENGTH);
    }

    private void fillOperator(ExceptionLog exceptionLog) {
        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        if (authentication == null
                || !(authentication.getPrincipal() instanceof UserDTO user)) {
            return;
        }

        exceptionLog.setOperatorId(user.getId());
        exceptionLog.setOperatorName(user.getNickname());
    }

    private String truncate(String value, int maxLength) {
        if (value == null || value.length() <= maxLength) {
            return value;
        }
        return value.substring(0, maxLength);
    }

    private ExceptionLogDTO toDTO(ExceptionLog log) {
        ExceptionLogDTO dto = new ExceptionLogDTO();
        dto.setId(log.getId());
        dto.setRequestMethod(log.getRequestMethod());
        dto.setRequestUri(log.getRequestUri());
        dto.setHandlerMethod(log.getHandlerMethod());
        dto.setRequestParams(log.getRequestParams());
        dto.setExceptionType(log.getExceptionType());
        dto.setExceptionMessage(log.getExceptionMessage());
        dto.setStackTrace(log.getStackTrace());
        dto.setOperatorId(log.getOperatorId());
        dto.setOperatorName(log.getOperatorName());
        dto.setIpAddress(log.getIpAddress());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}