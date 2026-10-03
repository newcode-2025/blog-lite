package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bloglite.dto.OperationLogDTO;
import com.example.bloglite.dto.OperationLogDeleteRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.entity.OperationLog;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.OperationLogMapper;
import com.example.bloglite.service.OperationLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 后台操作日志服务实现
 */
@Service
public class OperationLogServiceImpl implements OperationLogService {

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;
    private static final int MAX_DELETE_COUNT = 100;

    private final OperationLogMapper operationLogMapper;

    public OperationLogServiceImpl(OperationLogMapper operationLogMapper) {
        this.operationLogMapper = operationLogMapper;
    }

    @Override
    public PageResultDTO<OperationLogDTO> listLogs(
            String module,
            String action,
            String operatorName,
            Integer success,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long current,
            long size
    ) {
        validateSuccess(success);

        if (startTime != null && endTime != null
                && startTime.isAfter(endTime)) {
            throw new BizException(40000, "开始时间不能晚于结束时间");
        }

        long normalizedCurrent = current < 1 ? 1 : current;
        long normalizedSize = size < 1
                ? DEFAULT_SIZE
                : Math.min(size, MAX_SIZE);
        long offset = (normalizedCurrent - 1) * normalizedSize;

        LambdaQueryWrapper<OperationLog> countWrapper =
                buildWrapper(
                        module,
                        action,
                        operatorName,
                        success,
                        startTime,
                        endTime
                );
        Long count = operationLogMapper.selectCount(countWrapper);

        LambdaQueryWrapper<OperationLog> listWrapper =
                buildWrapper(
                        module,
                        action,
                        operatorName,
                        success,
                        startTime,
                        endTime
                )
                .orderByDesc(OperationLog::getCreatedAt)
                .orderByDesc(OperationLog::getId)
                .last("LIMIT " + offset + "," + normalizedSize);

        List<OperationLogDTO> records = operationLogMapper
                .selectList(listWrapper)
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
    public void deleteLogs(OperationLogDeleteRequest request) {
        if (request == null || request.getIds() == null
                || request.getIds().isEmpty()) {
            throw new BizException(40000, "操作日志 ID 列表不能为空");
        }

        if (request.getIds().size() > MAX_DELETE_COUNT) {
            throw new BizException(40000, "一次最多删除 100 条操作日志");
        }

        Set<Long> uniqueIds = new HashSet<>();

        for (Long id : request.getIds()) {
            if (id == null || id < 1) {
                throw new BizException(40000, "操作日志 ID 必须大于 0");
            }
            if (!uniqueIds.add(id)) {
                throw new BizException(40000, "操作日志 ID 不能重复");
            }
        }

        operationLogMapper.deleteByIds(uniqueIds);
    }

    private LambdaQueryWrapper<OperationLog> buildWrapper(
            String module,
            String action,
            String operatorName,
            Integer success,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return new LambdaQueryWrapper<OperationLog>()
                .like(StringUtils.hasText(module),
                        OperationLog::getModule,
                        module == null ? null : module.trim())
                .like(StringUtils.hasText(action),
                        OperationLog::getOperationAction,
                        action == null ? null : action.trim())
                .like(StringUtils.hasText(operatorName),
                        OperationLog::getOperatorName,
                        operatorName == null ? null : operatorName.trim())
                .eq(success != null, OperationLog::getSuccess, success)
                .ge(startTime != null,
                        OperationLog::getCreatedAt,
                        startTime)
                .le(endTime != null,
                        OperationLog::getCreatedAt,
                        endTime);
    }

    private void validateSuccess(Integer success) {
        if (success != null
                && !Integer.valueOf(0).equals(success)
                && !Integer.valueOf(1).equals(success)) {
            throw new BizException(40000, "操作结果只能是 0 或 1");
        }
    }

    private OperationLogDTO toDTO(OperationLog log) {
        OperationLogDTO dto = new OperationLogDTO();
        dto.setId(log.getId());
        dto.setModule(log.getModule());
        dto.setOperationAction(log.getOperationAction());
        dto.setDescription(log.getDescription());
        dto.setRequestMethod(log.getRequestMethod());
        dto.setRequestUri(log.getRequestUri());
        dto.setHandlerMethod(log.getHandlerMethod());
        dto.setRequestParams(log.getRequestParams());
        dto.setResponseCode(log.getResponseCode());
        dto.setSuccess(log.getSuccess());
        dto.setOperatorId(log.getOperatorId());
        dto.setOperatorName(log.getOperatorName());
        dto.setIpAddress(log.getIpAddress());
        dto.setDurationMs(log.getDurationMs());
        dto.setErrorMessage(log.getErrorMessage());
        dto.setCreatedAt(log.getCreatedAt());
        return dto;
    }
}