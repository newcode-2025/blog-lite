package com.example.bloglite.service.impl;

import com.example.bloglite.dto.AdminArticleListItemDTO;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.AdminArticleMapper;
import com.example.bloglite.service.AdminArticleService;
import com.example.bloglite.util.UserContext;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 后台文章业务实现
 */
@Service
public class AdminArticleServiceImpl implements AdminArticleService {

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private static final int STATUS_PUBLIC = 1;
    private static final int STATUS_PASSWORD = 2;
    private static final int STATUS_DRAFT = 3;

    private final AdminArticleMapper adminArticleMapper;

    public AdminArticleServiceImpl(
            AdminArticleMapper adminArticleMapper
    ) {
        this.adminArticleMapper = adminArticleMapper;
    }

    @Override
    public PageResultDTO<AdminArticleListItemDTO> listArticles(
            Integer status,
            String keywords,
            long current,
            long size
    ) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);

        validateStatus(status);

        UserDTO currentUser = UserContext.getCurrentUser();

        if (currentUser.getId() == null || currentUser.getId() < 1) {
            throw new BizException(
                    40100,
                    "当前登录用户信息不正确"
            );
        }

        String normalizedKeywords =
                keywords == null ? null : keywords.trim();

        if (normalizedKeywords != null
                && normalizedKeywords.isEmpty()) {
            normalizedKeywords = null;
        }

        long offset = (current - 1) * size;

        List<AdminArticleListItemDTO> records =
                adminArticleMapper.selectAdminArticles(
                        currentUser.getId(),
                        status,
                        normalizedKeywords,
                        offset,
                        size
                );

        long count = adminArticleMapper.countAdminArticles(
                currentUser.getId(),
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
     * 校验后台文章状态
     */
    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }

        if (status != STATUS_PUBLIC
                && status != STATUS_PASSWORD
                && status != STATUS_DRAFT) {
            throw new BizException(
                    40000,
                    "文章状态只能是 1、2 或 3"
            );
        }
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