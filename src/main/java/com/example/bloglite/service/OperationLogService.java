package com.example.bloglite.service;

import com.example.bloglite.dto.OperationLogDTO;
import com.example.bloglite.dto.OperationLogDeleteRequest;
import com.example.bloglite.dto.PageResultDTO;

import java.time.LocalDateTime;

/**
 * 后台操作日志服务
 */
public interface OperationLogService {

    /**
     * 分页查询操作日志
     */
    PageResultDTO<OperationLogDTO> listLogs(
            String module,
            String action,
            String operatorName,
            Integer success,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long current,
            long size
    );

    /**
     * 批量删除操作日志
     */
    void deleteLogs(OperationLogDeleteRequest request);
}