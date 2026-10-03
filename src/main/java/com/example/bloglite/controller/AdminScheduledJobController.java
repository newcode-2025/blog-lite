package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.ScheduledJobDTO;
import com.example.bloglite.dto.ScheduledJobLogDTO;
import com.example.bloglite.dto.ScheduledJobSaveRequest;
import com.example.bloglite.dto.ScheduledJobStatusUpdateRequest;
import com.example.bloglite.dto.TaskHandlerOptionDTO;
import com.example.bloglite.service.ScheduledJobService;
import jakarta.validation.Valid;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 后台定时任务管理接口
 */
@RestController
@RequestMapping("/api/v1/admin/jobs")
@PreAuthorize("hasRole('ADMIN')")
public class AdminScheduledJobController {

    private final ScheduledJobService scheduledJobService;

    public AdminScheduledJobController(
            ScheduledJobService scheduledJobService
    ) {
        this.scheduledJobService = scheduledJobService;
    }

    @GetMapping
    public ApiResponse<PageResultDTO<ScheduledJobDTO>> listJobs(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                scheduledJobService.listJobs(
                        status,
                        keywords,
                        current,
                        size
                )
        );
    }

    @GetMapping("/handlers")
    public ApiResponse<List<TaskHandlerOptionDTO>> listHandlers() {
        return ApiResponse.success(scheduledJobService.listHandlers());
    }

    @GetMapping("/{jobId}")
    public ApiResponse<ScheduledJobDTO> getJob(
            @PathVariable Long jobId
    ) {
        return ApiResponse.success(scheduledJobService.getJob(jobId));
    }

    @PostMapping
    public ApiResponse<ScheduledJobDTO> createJob(
            @Valid @RequestBody ScheduledJobSaveRequest request
    ) {
        return ApiResponse.success(scheduledJobService.createJob(request));
    }

    @PutMapping("/{jobId}")
    public ApiResponse<ScheduledJobDTO> updateJob(
            @PathVariable Long jobId,
            @Valid @RequestBody ScheduledJobSaveRequest request
    ) {
        return ApiResponse.success(
                scheduledJobService.updateJob(jobId, request)
        );
    }

    @PutMapping("/{jobId}/status")
    public ApiResponse<ScheduledJobDTO> updateStatus(
            @PathVariable Long jobId,
            @Valid @RequestBody ScheduledJobStatusUpdateRequest request
    ) {
        return ApiResponse.success(
                scheduledJobService.updateStatus(jobId, request)
        );
    }

    @PostMapping("/{jobId}/run")
    public ApiResponse<ScheduledJobLogDTO> runOnce(
            @PathVariable Long jobId
    ) {
        return ApiResponse.success(
                scheduledJobService.runOnce(jobId)
        );
    }

    @DeleteMapping("/{jobId}")
    public ApiResponse<Void> deleteJob(
            @PathVariable Long jobId
    ) {
        scheduledJobService.deleteJob(jobId);
        return ApiResponse.success(null);
    }
}