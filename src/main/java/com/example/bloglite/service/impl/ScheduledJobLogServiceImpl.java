package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ScheduledJobLogDTO;
import com.example.bloglite.dto.ScheduledJobLogDeleteRequest;
import com.example.bloglite.entity.ScheduledJobLog;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ScheduledJobLogMapper;
import com.example.bloglite.service.ScheduledJobLogService;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 定时任务执行日志服务实现
 */
@Service
public class ScheduledJobLogServiceImpl implements ScheduledJobLogService {

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;
    private static final int MAX_DELETE_COUNT = 100;

    private final ScheduledJobLogMapper scheduledJobLogMapper;

    public ScheduledJobLogServiceImpl(
            ScheduledJobLogMapper scheduledJobLogMapper
    ) {
        this.scheduledJobLogMapper = scheduledJobLogMapper;
    }

    @Override
    public PageResultDTO<ScheduledJobLogDTO> listLogs(
            Long jobId,
            String taskKey,
            Integer status,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long current,
            long size
    ) {
        if (jobId != null && jobId < 1) {
            throw new BizException(40000, "任务 ID 不正确");
        }

        validateStatus(status);

        if (startTime != null && endTime != null
                && startTime.isAfter(endTime)) {
            throw new BizException(40000, "开始时间不能晚于结束时间");
        }

        long normalizedCurrent = current < 1 ? 1 : current;
        long normalizedSize = size < 1
                ? DEFAULT_SIZE
                : Math.min(size, MAX_SIZE);
        long offset = (normalizedCurrent - 1) * normalizedSize;

        Long count = scheduledJobLogMapper.selectCount(
                buildWrapper(
                        jobId,
                        taskKey,
                        status,
                        startTime,
                        endTime
                )
        );
        List<ScheduledJobLogDTO> records = scheduledJobLogMapper.selectList(
                        buildWrapper(
                                jobId,
                                taskKey,
                                status,
                                startTime,
                                endTime
                        )
                                .orderByDesc(ScheduledJobLog::getCreatedAt)
                                .orderByDesc(ScheduledJobLog::getId)
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
    public void deleteLogs(ScheduledJobLogDeleteRequest request) {
        if (request == null || request.getIds() == null
                || request.getIds().isEmpty()) {
            throw new BizException(40000, "任务日志 ID 列表不能为空");
        }

        if (request.getIds().size() > MAX_DELETE_COUNT) {
            throw new BizException(40000, "一次最多删除 100 条任务日志");
        }

        Set<Long> uniqueIds = new HashSet<>();

        for (Long id : request.getIds()) {
            if (id == null || id < 1) {
                throw new BizException(40000, "任务日志 ID 必须大于 0");
            }
            if (!uniqueIds.add(id)) {
                throw new BizException(40000, "任务日志 ID 不能重复");
            }
        }

        scheduledJobLogMapper.deleteByIds(uniqueIds);
    }

    private LambdaQueryWrapper<ScheduledJobLog> buildWrapper(
            Long jobId,
            String taskKey,
            Integer status,
            LocalDateTime startTime,
            LocalDateTime endTime
    ) {
        return new LambdaQueryWrapper<ScheduledJobLog>()
                .eq(jobId != null, ScheduledJobLog::getJobId, jobId)
                .like(StringUtils.hasText(taskKey),
                        ScheduledJobLog::getTaskKey,
                        taskKey == null ? null : taskKey.trim())
                .eq(status != null, ScheduledJobLog::getStatus, status)
                .ge(startTime != null,
                        ScheduledJobLog::getStartTime,
                        startTime)
                .le(endTime != null,
                        ScheduledJobLog::getStartTime,
                        endTime);
    }

    private void validateStatus(Integer status) {
        if (status != null
                && !Integer.valueOf(0).equals(status)
                && !Integer.valueOf(1).equals(status)) {
            throw new BizException(40000, "任务执行状态只能是 0 或 1");
        }
    }

    private ScheduledJobLogDTO toDTO(ScheduledJobLog jobLog) {
        ScheduledJobLogDTO dto = new ScheduledJobLogDTO();
        dto.setId(jobLog.getId());
        dto.setJobId(jobLog.getJobId());
        dto.setTaskKey(jobLog.getTaskKey());
        dto.setJobName(jobLog.getJobName());
        dto.setJobGroup(jobLog.getJobGroup());
        dto.setTriggerType(jobLog.getTriggerType());
        dto.setStatus(jobLog.getStatus());
        dto.setJobMessage(jobLog.getJobMessage());
        dto.setExceptionInfo(jobLog.getExceptionInfo());
        dto.setStartTime(jobLog.getStartTime());
        dto.setEndTime(jobLog.getEndTime());
        dto.setDurationMs(jobLog.getDurationMs());
        dto.setCreatedAt(jobLog.getCreatedAt());
        return dto;
    }
}