package com.example.bloglite.service.impl;

import com.example.bloglite.entity.ExceptionLog;
import com.example.bloglite.mapper.ExceptionLogMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

/**
 * 系统异常日志异步持久化服务
 */
@Slf4j
@Service
public class ExceptionLogPersistenceService {

    private final ExceptionLogMapper exceptionLogMapper;

    public ExceptionLogPersistenceService(
            ExceptionLogMapper exceptionLogMapper
    ) {
        this.exceptionLogMapper = exceptionLogMapper;
    }

    /**
     * 异步保存异常日志，日志失败不能覆盖原异常处理结果
     */
    @Async("exceptionLogExecutor")
    public void save(ExceptionLog exceptionLog) {
        try {
            exceptionLogMapper.insert(exceptionLog);
        } catch (RuntimeException exception) {
            log.error("异常日志保存失败", exception);
        }
    }
}