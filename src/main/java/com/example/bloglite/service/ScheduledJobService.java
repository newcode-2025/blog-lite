package com.example.bloglite.service;

import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ScheduledJobDTO;
import com.example.bloglite.dto.ScheduledJobLogDTO;
import com.example.bloglite.dto.ScheduledJobSaveRequest;
import com.example.bloglite.dto.ScheduledJobStatusUpdateRequest;
import com.example.bloglite.dto.TaskHandlerOptionDTO;

import java.util.List;

/**
 * 定时任务管理服务
 */
public interface ScheduledJobService {

    PageResultDTO<ScheduledJobDTO> listJobs(
            Integer status,
            String keywords,
            long current,
            long size
    );

    List<TaskHandlerOptionDTO> listHandlers();

    ScheduledJobDTO getJob(Long jobId);

    ScheduledJobDTO createJob(ScheduledJobSaveRequest request);

    ScheduledJobDTO updateJob(
            Long jobId,
            ScheduledJobSaveRequest request
    );

    ScheduledJobDTO updateStatus(
            Long jobId,
            ScheduledJobStatusUpdateRequest request
    );

    ScheduledJobLogDTO runOnce(Long jobId);

    void deleteJob(Long jobId);

    void reloadScheduledJobs();
}