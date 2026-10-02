package com.example.bloglite.service;

import com.example.bloglite.dto.AdminTagCreateRequest;
import com.example.bloglite.dto.AdminTagDTO;
import com.example.bloglite.dto.AdminTagUpdateRequest;

import java.util.List;

/**
 * 后台标签业务接口
 */
public interface AdminTagService {

    /**
     * 查询全部标签
     */
    List<AdminTagDTO> listTags();

    /**
     * 新建标签
     */
    AdminTagDTO createTag(AdminTagCreateRequest request);

    /**
     * 编辑标签
     */
    AdminTagDTO updateTag(
            Long tagId,
            AdminTagUpdateRequest request
    );

    /**
     * 删除未被文章使用的标签
     */
    void deleteTag(Long tagId);
}