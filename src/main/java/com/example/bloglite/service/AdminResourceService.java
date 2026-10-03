package com.example.bloglite.service;

import com.example.bloglite.dto.AdminResourceDTO;
import com.example.bloglite.dto.AdminResourceSaveRequest;

import java.util.List;

/**
 * 后台接口资源业务接口
 */
public interface AdminResourceService {

    /**
     * 查询完整接口资源树
     */
    List<AdminResourceDTO> listResourceTree();

    /**
     * 查询接口资源详情
     */
    AdminResourceDTO getResourceDetail(Long resourceId);

    /**
     * 新增接口资源
     */
    AdminResourceDTO createResource(AdminResourceSaveRequest request);

    /**
     * 编辑接口资源
     */
    AdminResourceDTO updateResource(
            Long resourceId,
            AdminResourceSaveRequest request
    );

    /**
     * 删除未被使用的接口资源
     */
    void deleteResource(Long resourceId);
}
