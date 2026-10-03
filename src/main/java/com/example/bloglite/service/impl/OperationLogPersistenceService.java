package com.example.bloglite.service.impl;

import com.example.bloglite.entity.OperationLog;
import com.example.bloglite.mapper.OperationLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 操作日志异步持久化服务
 */
@Slf4j
@Service
public class OperationLogPersistenceService {

    private final OperationLogMapper operationLogMapper;

    public OperationLogPersistenceService(
            OperationLogMapper operationLogMapper
    ) {
        this.operationLogMapper = operationLogMapper;
    }

    /**
     * 异步保存操作日志，日志失败不能影响原业务
     */
    @Async("operationLogExecutor")
    public void save(OperationLog operationLog) {
        try {
            operationLogMapper.insert(operationLog);
        } catch (RuntimeException exception) {
            log.error("操作日志保存失败", exception);
        }
    }
}