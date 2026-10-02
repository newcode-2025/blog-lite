package com.example.bloglite.service;

import com.example.bloglite.dto.AdminTalkSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.TalkDTO;

/**
 * 说说业务接口
 */
public interface TalkService {

    /**
     * 分页查询公开说说
     */
    PageResultDTO<TalkDTO> listPublicTalks(
            long current,
            long size
    );

    /**
     * 后台分页查询说说
     */
    PageResultDTO<TalkDTO> listAdminTalks(
            Integer status,
            String keywords,
            long current,
            long size
    );

    /**
     * 新建说说
     */
    TalkDTO createTalk(AdminTalkSaveRequest request);

    /**
     * 编辑说说
     */
    TalkDTO updateTalk(
            Long talkId,
            AdminTalkSaveRequest request
    );

    /**
     * 软删除说说
     */
    void deleteTalk(Long talkId);
}