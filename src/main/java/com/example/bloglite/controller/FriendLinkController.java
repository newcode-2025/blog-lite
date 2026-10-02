package com.example.bloglite.controller;

import com.example.bloglite.common.ApiResponse;
import com.example.bloglite.dto.AdminFriendLinkSaveRequest;
import com.example.bloglite.dto.FriendLinkDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.service.FriendLinkService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 友情链接接口
 */
@RestController
public class FriendLinkController {

    private final FriendLinkService friendLinkService;

    public FriendLinkController(
            FriendLinkService friendLinkService
    ) {
        this.friendLinkService = friendLinkService;
    }

    /**
     * 查询全部显示状态的友链
     */
    @GetMapping("/api/v1/links")
    public ApiResponse<List<FriendLinkDTO>> listPublicLinks() {
        return ApiResponse.success(
                friendLinkService.listPublicLinks()
        );
    }

    /**
     * 后台分页查询友链
     */
    @GetMapping("/api/v1/admin/links")
    public ApiResponse<PageResultDTO<FriendLinkDTO>>
    listAdminLinks(
            @RequestParam(required = false) Integer status,
            @RequestParam(defaultValue = "") String keywords,
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size
    ) {
        return ApiResponse.success(
                friendLinkService.listAdminLinks(
                        status,
                        keywords,
                        current,
                        size
                )
        );
    }

    /**
     * 新建友链
     */
    @PostMapping("/api/v1/admin/links")
    public ApiResponse<FriendLinkDTO> createLink(
            @Valid @RequestBody AdminFriendLinkSaveRequest request
    ) {
        return ApiResponse.success(
                friendLinkService.createLink(request)
        );
    }

    /**
     * 编辑友链
     */
    @PutMapping("/api/v1/admin/links/{linkId}")
    public ApiResponse<FriendLinkDTO> updateLink(
            @PathVariable Long linkId,
            @Valid @RequestBody AdminFriendLinkSaveRequest request
    ) {
        return ApiResponse.success(
                friendLinkService.updateLink(linkId, request)
        );
    }

    /**
     * 删除友链
     */
    @DeleteMapping("/api/v1/admin/links/{linkId}")
    public ApiResponse<Void> deleteLink(
            @PathVariable Long linkId
    ) {
        friendLinkService.deleteLink(linkId);
        return ApiResponse.success(null);
    }
}