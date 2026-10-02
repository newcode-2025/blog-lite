package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminTalkSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.TalkDTO;
import com.example.bloglite.service.TalkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 说说接口
 */
@RestController
public class TalkController {

    private final TalkService talkService;

    public TalkController(TalkService talkService) {
        this.talkService = talkService;
    }

    /**
     * 分页查询公开说说
     */
    @GetMapping("/api/v1/talks")
    public ApiResponse<PageResultDTO<TalkDTO>> listPublicTalks(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                talkService.listPublicTalks(current, size)
        );
    }

    /**
     * 后台分页查询说说
     */
    @GetMapping("/api/v1/admin/talks")
    public ApiResponse<PageResultDTO<TalkDTO>> listAdminTalks(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                talkService.listAdminTalks(
                        status,
                        keywords,
                        current,
                        size
                )
        );
    }

    /**
     * 新建说说
     */
    @PostMapping("/api/v1/admin/talks")
    public ApiResponse<TalkDTO> createTalk(
            @Valid @RequestBody AdminTalkSaveRequest request
    ) {
        return ApiResponse.success(
                talkService.createTalk(request)
        );
    }

    /**
     * 编辑说说
     */
    @PutMapping("/api/v1/admin/talks/{talkId}")
    public ApiResponse<TalkDTO> updateTalk(
            @PathVariable Long talkId,
            @Valid @RequestBody AdminTalkSaveRequest request
    ) {
        return ApiResponse.success(
                talkService.updateTalk(talkId, request)
        );
    }

    /**
     * 软删除说说
     */
    @DeleteMapping("/api/v1/admin/talks/{talkId}")
    public ApiResponse<Void> deleteTalk(
            @PathVariable Long talkId
    ) {
        talkService.deleteTalk(talkId);
        return ApiResponse.success(null);
    }
}