package com.example.bloglite.service.impl;

import com.example.bloglite.dto.AdminFriendLinkSaveRequest;
import com.example.bloglite.dto.FriendLinkDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.entity.FriendLink;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.FriendLinkMapper;
import com.example.bloglite.service.FriendLinkService;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 友情链接业务实现
 */
@Service
public class FriendLinkServiceImpl implements FriendLinkService {

    private static final int STATUS_SHOW = 1;
    private static final int STATUS_HIDE = 0;

    private static final int DEFAULT_SORT_ORDER = 0;

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private final FriendLinkMapper friendLinkMapper;

    public FriendLinkServiceImpl(
            FriendLinkMapper friendLinkMapper
    ) {
        this.friendLinkMapper = friendLinkMapper;
    }

    /**
     * 查询全部显示状态的友链
     */
    @Override
    public List<FriendLinkDTO> listPublicLinks() {
        return friendLinkMapper.selectPublicLinks();
    }

    /**
     * 后台分页查询友链
     */
    @Override
    public PageResultDTO<FriendLinkDTO> listAdminLinks(
            Integer status,
            String keywords,
            long current,
            long size
    ) {
        validateStatus(status);

        current = normalizeCurrent(current);
        size = normalizeSize(size);

        String normalizedKeywords = normalizeKeywords(keywords);

        long offset = (current - 1) * size;

        List<FriendLinkDTO> records =
                friendLinkMapper.selectAdminLinks(
                        status,
                        normalizedKeywords,
                        offset,
                        size
                );

        long count = friendLinkMapper.countAdminLinks(
                status,
                normalizedKeywords
        );

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    /**
     * 新建友链
     */
    @Transactional
    @Override
    public FriendLinkDTO createLink(
            AdminFriendLinkSaveRequest request
    ) {
        String name = normalizeRequiredText(
                request.getName(),
                "友链名称不能为空"
        );

        String address = normalizeRequiredText(
                request.getAddress(),
                "友链地址不能为空"
        );

        ensureAddressAvailable(address, null);

        int sortOrder = request.getSortOrder() == null
                ? DEFAULT_SORT_ORDER
                : request.getSortOrder();

        int status = request.getStatus() == null
                ? STATUS_SHOW
                : normalizeStatus(request.getStatus());

        LocalDateTime now = LocalDateTime.now();

        FriendLink link = new FriendLink();
        link.setName(name);
        link.setAvatarUrl(
                normalizeOptionalText(request.getAvatarUrl())
        );
        link.setAddress(address);
        link.setDescription(
                normalizeOptionalText(request.getDescription())
        );
        link.setSortOrder(sortOrder);
        link.setStatus(status);
        link.setCreatedAt(now);
        link.setUpdatedAt(now);

        int insertedRows;

        try {
            insertedRows = friendLinkMapper.insert(link);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "友链地址已存在"
            );
        }

        if (insertedRows != 1 || link.getId() == null) {
            throw new BizException(
                    50000,
                    "友链创建失败，请稍后重试"
            );
        }

        return loadLink(link.getId());
    }

    /**
     * 编辑友链
     */
    @Transactional
    @Override
    public FriendLinkDTO updateLink(
            Long linkId,
            AdminFriendLinkSaveRequest request
    ) {
        FriendLink existingLink = getLinkOrThrow(linkId);

        String name = normalizeRequiredText(
                request.getName(),
                "友链名称不能为空"
        );

        String address = normalizeRequiredText(
                request.getAddress(),
                "友链地址不能为空"
        );

        ensureAddressAvailable(address, linkId);

        int sortOrder = request.getSortOrder() == null
                ? existingLink.getSortOrder()
                : request.getSortOrder();

        int status = request.getStatus() == null
                ? existingLink.getStatus()
                : normalizeStatus(request.getStatus());

        FriendLink updateLink = new FriendLink();
        updateLink.setId(linkId);
        updateLink.setName(name);
        updateLink.setAvatarUrl(
                normalizeOptionalText(request.getAvatarUrl())
        );
        updateLink.setAddress(address);
        updateLink.setDescription(
                normalizeOptionalText(request.getDescription())
        );
        updateLink.setSortOrder(sortOrder);
        updateLink.setStatus(status);
        updateLink.setUpdatedAt(LocalDateTime.now());

        int updatedRows;

        try {
            updatedRows = friendLinkMapper.updateById(updateLink);
        } catch (DuplicateKeyException exception) {
            throw new BizException(
                    40900,
                    "友链地址已存在"
            );
        }

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "友链更新失败，请稍后重试"
            );
        }

        return loadLink(linkId);
    }

    /**
     * 删除友链
     */
    @Transactional
    @Override
    public void deleteLink(Long linkId) {
        getLinkOrThrow(linkId);

        int deletedRows = friendLinkMapper.deleteById(linkId);

        if (deletedRows < 1) {
            throw new BizException(
                    50000,
                    "友链删除失败，请稍后重试"
            );
        }
    }

    /**
     * 查询友链，不存在时抛出异常
     */
    private FriendLink getLinkOrThrow(Long linkId) {
        if (linkId == null || linkId < 1) {
            throw new BizException(40000, "友链 ID 不正确");
        }

        FriendLink link = friendLinkMapper.selectById(linkId);

        if (link == null) {
            throw new BizException(40400, "友链不存在");
        }

        return link;
    }

    /**
     * 重新查询友链数据
     */
    private FriendLinkDTO loadLink(Long linkId) {
        FriendLinkDTO link =
                friendLinkMapper.selectAdminLinkById(linkId);

        if (link == null) {
            throw new BizException(
                    50000,
                    "友链数据读取失败，请稍后重试"
            );
        }

        return link;
    }

    /**
     * 校验状态
     */
    private void validateStatus(Integer status) {
        if (status != null) {
            normalizeStatus(status);
        }
    }

    /**
     * 校验并返回状态
     */
    private int normalizeStatus(Integer status) {
        if (status == null
                || (status != STATUS_SHOW
                && status != STATUS_HIDE)) {
            throw new BizException(
                    40000,
                    "友链状态只能是 0 或 1"
            );
        }

        return status;
    }

    /**
     * 校验地址是否重复
     */
    private void ensureAddressAvailable(
            String address,
            Long excludeId
    ) {
        long count = friendLinkMapper.countByAddress(
                address,
                excludeId
        );

        if (count > 0) {
            throw new BizException(
                    40900,
                    "友链地址已存在"
            );
        }
    }

    /**
     * 校验必填文本
     */
    private String normalizeRequiredText(
            String value,
            String message
    ) {
        if (value == null || value.trim().isEmpty()) {
            throw new BizException(40000, message);
        }

        return value.trim();
    }

    /**
     * 清理可选文本
     */
    private String normalizeOptionalText(String value) {
        if (value == null) {
            return null;
        }

        String normalizedValue = value.trim();

        return normalizedValue.isEmpty() ? null : normalizedValue;
    }

    /**
     * 规范化搜索关键词
     */
    private String normalizeKeywords(String keywords) {
        if (keywords == null) {
            return null;
        }

        String normalizedKeywords = keywords.trim();

        return normalizedKeywords.isEmpty()
                ? null
                : normalizedKeywords;
    }

    /**
     * 规范化当前页码
     */
    private long normalizeCurrent(long current) {
        return current < 1 ? 1 : current;
    }

    /**
     * 规范化分页大小
     */
    private long normalizeSize(long size) {
        if (size < 1) {
            return DEFAULT_SIZE;
        }

        if (size > MAX_SIZE) {
            return MAX_SIZE;
        }

        return size;
    }
}