package com.example.bloglite.service.impl;

import com.example.bloglite.dto.AdminTagCreateRequest;
import com.example.bloglite.dto.AdminTagDTO;
import com.example.bloglite.dto.AdminTagUpdateRequest;
import com.example.bloglite.entity.Tag;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.AdminTagMapper;
import com.example.bloglite.service.AdminTagService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 后台标签业务实现
 */
@Service
public class AdminTagServiceImpl implements AdminTagService {

    private final AdminTagMapper adminTagMapper;

    public AdminTagServiceImpl(AdminTagMapper adminTagMapper) {
        this.adminTagMapper = adminTagMapper;
    }

    /**
     * 查询全部标签
     */
    @Override
    public List<AdminTagDTO> listTags() {
        return adminTagMapper.selectAdminTags();
    }

    /**
     * 新建标签
     */
    @Transactional
    @Override
    public AdminTagDTO createTag(AdminTagCreateRequest request) {
        String name = normalizeName(request.getName());

        ensureNameAvailable(name, null);

        Tag tag = new Tag();
        tag.setName(name);

        int insertedRows;

        try {
            insertedRows = adminTagMapper.insert(tag);
        } catch (DuplicateKeyException exception) {
            throw new BizException(40900, "标签名称已存在");
        }

        if (insertedRows != 1 || tag.getId() == null) {
            throw new BizException(
                    50000,
                    "标签创建失败，请稍后重试"
            );
        }

        return loadTag(tag.getId());
    }

    /**
     * 编辑标签
     */
    @Transactional
    @Override
    public AdminTagDTO updateTag(
            Long tagId,
            AdminTagUpdateRequest request
    ) {
        getTagOrThrow(tagId);

        String name = normalizeName(request.getName());

        ensureNameAvailable(name, tagId);

        Tag tag = new Tag();
        tag.setId(tagId);
        tag.setName(name);

        int updatedRows;

        try {
            updatedRows = adminTagMapper.updateById(tag);
        } catch (DuplicateKeyException exception) {
            throw new BizException(40900, "标签名称已存在");
        }

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "标签更新失败，请稍后重试"
            );
        }

        return loadTag(tagId);
    }

    /**
     * 删除未被文章使用的标签
     */
    @Transactional
    @Override
    public void deleteTag(Long tagId) {
        getTagOrThrow(tagId);

        long articleCount =
                adminTagMapper.countArticleTagsByTagId(tagId);

        if (articleCount > 0) {
            throw new BizException(
                    40900,
                    "标签已被文章使用，不能删除"
            );
        }

        int deletedRows = adminTagMapper.deleteById(tagId);

        if (deletedRows < 1) {
            throw new BizException(
                    50000,
                    "标签删除失败，请稍后重试"
            );
        }
    }

    /**
     * 查询标签，不存在时抛出业务异常
     */
    private Tag getTagOrThrow(Long tagId) {
        validateTagId(tagId);

        Tag tag = adminTagMapper.selectById(tagId);

        if (tag == null) {
            throw new BizException(40400, "标签不存在");
        }

        return tag;
    }

    /**
     * 重新查询标签管理数据
     */
    private AdminTagDTO loadTag(Long tagId) {
        AdminTagDTO tag =
                adminTagMapper.selectAdminTagById(tagId);

        if (tag == null) {
            throw new BizException(
                    50000,
                    "标签数据读取失败，请稍后重试"
            );
        }

        return tag;
    }

    /**
     * 校验标签 ID
     */
    private void validateTagId(Long tagId) {
        if (tagId == null || tagId < 1) {
            throw new BizException(40000, "标签 ID 不正确");
        }
    }

    /**
     * 校验标签名称
     */
    private String normalizeName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new BizException(
                    40000,
                    "标签名称不能为空"
            );
        }

        return name.trim();
    }

    /**
     * 校验标签名称是否重复
     */
    private void ensureNameAvailable(
            String name,
            Long excludeId
    ) {
        long count = adminTagMapper.countByTagName(
                name,
                excludeId
        );

        if (count > 0) {
            throw new BizException(
                    40900,
                    "标签名称已存在"
            );
        }
    }
}