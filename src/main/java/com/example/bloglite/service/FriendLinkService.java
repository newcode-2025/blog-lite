package com.example.bloglite.service;

import com.example.bloglite.dto.AdminFriendLinkSaveRequest;
import com.example.bloglite.dto.FriendLinkDTO;
import com.example.bloglite.dto.PageResultDTO;

import java.util.List;

/**
 * 友情链接业务接口
 */
public interface FriendLinkService {

    /**
     * 查询全部显示状态的友链
     */
    List<FriendLinkDTO> listPublicLinks();

    /**
     * 后台分页查询友链
     */
    PageResultDTO<FriendLinkDTO> listAdminLinks(
            Integer status,
            String keywords,
            long current,
            long size
    );

    /**
     * 新建友链
     */
    FriendLinkDTO createLink(AdminFriendLinkSaveRequest request);

    /**
     * 编辑友链
     */
    FriendLinkDTO updateLink(
            Long linkId,
            AdminFriendLinkSaveRequest request
    );

    /**
     * 删除友链
     */
    void deleteLink(Long linkId);
}