package com.example.bloglite.service;

import com.example.bloglite.dto.AdminArticleDetailDTO;
import com.example.bloglite.dto.AdminArticleFlagsUpdateRequest;
import com.example.bloglite.dto.AdminArticleListItemDTO;
import com.example.bloglite.dto.AdminArticleStatusUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;

/**
 * 后台文章生命周期业务接口
 */
public interface AdminArticleLifecycleService {

    /**
     * 修改文章状态
     */
    AdminArticleDetailDTO updateStatus(
            Long articleId,
            AdminArticleStatusUpdateRequest request
    );

    /**
     * 修改置顶和推荐
     */
    AdminArticleDetailDTO updateFlags(
            Long articleId,
            AdminArticleFlagsUpdateRequest request
    );

    /**
     * 软删除文章
     */
    void softDeleteArticle(Long articleId);

    /**
     * 恢复软删除文章
     */
    AdminArticleDetailDTO restoreArticle(Long articleId);

    /**
     * 分页查询当前用户回收站
     */
    PageResultDTO<AdminArticleListItemDTO> listRecycleBin(
            String keywords,
            long current,
            long size
    );
}