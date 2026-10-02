package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.AdminArticleDetailDTO;
import com.example.bloglite.dto.AdminArticleFlagsUpdateRequest;
import com.example.bloglite.dto.AdminArticleListItemDTO;
import com.example.bloglite.dto.AdminArticleStatusUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.Article;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.AdminArticleMapper;
import com.example.bloglite.service.AdminArticleLifecycleService;
import com.example.bloglite.service.AdminArticleService;
import com.example.bloglite.util.UserContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

/**
 * 后台文章生命周期业务实现
 */
@Service
public class AdminArticleLifecycleServiceImpl
        implements AdminArticleLifecycleService {

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private static final int STATUS_PUBLIC = 1;
    private static final int STATUS_PASSWORD = 2;
    private static final int STATUS_DRAFT = 3;

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private final AdminArticleMapper adminArticleMapper;
    private final AdminArticleService adminArticleService;
    private final PasswordEncoder passwordEncoder;

    public AdminArticleLifecycleServiceImpl(
            AdminArticleMapper adminArticleMapper,
            AdminArticleService adminArticleService,
            PasswordEncoder passwordEncoder
    ) {
        this.adminArticleMapper = adminArticleMapper;
        this.adminArticleService = adminArticleService;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 修改文章状态
     */
    @Transactional
    @Override
    public AdminArticleDetailDTO updateStatus(
            Long articleId,
            AdminArticleStatusUpdateRequest request
    ) {
        UserDTO currentUser = getCurrentUser();

        Article existingArticle = getOwnedArticle(
                articleId,
                currentUser.getId(),
                false
        );

        int status = normalizeStatus(request.getStatus());

        String encodedAccessPassword = resolveAccessPassword(
                status,
                request.getAccessPassword(),
                existingArticle.getAccessPassword()
        );

        LocalDateTime publishedAt = resolvePublishedAt(
                existingArticle,
                status,
                LocalDateTime.now()
        );

        LambdaUpdateWrapper<Article> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Article::getId, articleId)
                .eq(Article::getAuthorId, currentUser.getId())
                .eq(Article::getIsDeleted, NOT_DELETED)
                .set(
                        Article::getStatus,
                        status
                )
                .set(
                        Article::getAccessPassword,
                        encodedAccessPassword
                )
                .set(
                        Article::getPublishedAt,
                        publishedAt
                );

        adminArticleMapper.update(null, updateWrapper);

        return adminArticleService.getArticleDetail(articleId);
    }

    /**
     * 修改文章置顶和推荐
     */
    @Transactional
    @Override
    public AdminArticleDetailDTO updateFlags(
            Long articleId,
            AdminArticleFlagsUpdateRequest request
    ) {
        UserDTO currentUser = getCurrentUser();

        getOwnedArticle(
                articleId,
                currentUser.getId(),
                false
        );

        LambdaUpdateWrapper<Article> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Article::getId, articleId)
                .eq(Article::getAuthorId, currentUser.getId())
                .eq(Article::getIsDeleted, NOT_DELETED)
                .set(
                        Article::getIsTop,
                        request.getIsTop()
                )
                .set(
                        Article::getIsFeatured,
                        request.getIsFeatured()
                );

        adminArticleMapper.update(null, updateWrapper);

        return adminArticleService.getArticleDetail(articleId);
    }

    /**
     * 软删除文章
     */
    @Transactional
    @Override
    public void softDeleteArticle(Long articleId) {
        UserDTO currentUser = getCurrentUser();

        Article article = getOwnedArticle(
                articleId,
                currentUser.getId(),
                true
        );

        if (isDeleted(article)) {
            return;
        }

        LambdaUpdateWrapper<Article> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Article::getId, articleId)
                .eq(Article::getAuthorId, currentUser.getId())
                .eq(Article::getIsDeleted, NOT_DELETED)
                .set(
                        Article::getIsDeleted,
                        DELETED
                );

        int updatedRows = adminArticleMapper.update(
                null,
                updateWrapper
        );

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "文章删除失败，请稍后重试"
            );
        }
    }

    /**
     * 恢复软删除文章
     */
    @Transactional
    @Override
    public AdminArticleDetailDTO restoreArticle(Long articleId) {
        UserDTO currentUser = getCurrentUser();

        Article article = getOwnedArticle(
                articleId,
                currentUser.getId(),
                true
        );

        if (!isDeleted(article)) {
            return adminArticleService.getArticleDetail(articleId);
        }

        LambdaUpdateWrapper<Article> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Article::getId, articleId)
                .eq(Article::getAuthorId, currentUser.getId())
                .eq(Article::getIsDeleted, DELETED)
                .set(
                        Article::getIsDeleted,
                        NOT_DELETED
                );

        int updatedRows = adminArticleMapper.update(
                null,
                updateWrapper
        );

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "文章恢复失败，请稍后重试"
            );
        }

        return adminArticleService.getArticleDetail(articleId);
    }

    /**
     * 分页查询当前用户回收站
     */
    @Override
    public PageResultDTO<AdminArticleListItemDTO> listRecycleBin(
            String keywords,
            long current,
            long size
    ) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);

        UserDTO currentUser = getCurrentUser();
        String normalizedKeywords = normalizeKeywords(keywords);

        long offset = (current - 1) * size;

        List<AdminArticleListItemDTO> records =
                adminArticleMapper.selectDeletedAdminArticles(
                        currentUser.getId(),
                        normalizedKeywords,
                        offset,
                        size
                );

        long count = adminArticleMapper.countDeletedAdminArticles(
                currentUser.getId(),
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
     * 获取当前登录用户
     */
    private UserDTO getCurrentUser() {
        UserDTO currentUser = UserContext.getCurrentUser();

        if (currentUser.getId() == null || currentUser.getId() < 1) {
            throw new BizException(
                    40100,
                    "当前登录用户信息不正确"
            );
        }

        return currentUser;
    }

    /**
     * 查询当前用户拥有的文章
     */
    private Article getOwnedArticle(
            Long articleId,
            Long currentUserId,
            boolean includeDeleted
    ) {
        if (articleId == null || articleId < 1) {
            throw new BizException(40000, "文章 ID 不正确");
        }

        Article article = adminArticleMapper.selectById(articleId);

        if (article == null) {
            throw new BizException(40400, "文章不存在");
        }

        if (!includeDeleted && isDeleted(article)) {
            throw new BizException(40400, "文章不存在");
        }

        if (!Objects.equals(
                article.getAuthorId(),
                currentUserId
        )) {
            throw new BizException(
                    40300,
                    "无权操作这篇文章"
            );
        }

        return article;
    }

    /**
     * 判断文章是否已软删除
     */
    private boolean isDeleted(Article article) {
        return Integer.valueOf(DELETED)
                .equals(article.getIsDeleted());
    }

    /**
     * 校验文章状态
     */
    private int normalizeStatus(Integer status) {
        if (status == null) {
            throw new BizException(
                    40000,
                    "文章状态不能为空"
            );
        }

        if (status != STATUS_PUBLIC
                && status != STATUS_PASSWORD
                && status != STATUS_DRAFT) {
            throw new BizException(
                    40000,
                    "文章状态只能是 1、2 或 3"
            );
        }

        return status;
    }

    /**
     * 处理访问密码
     */
    private String resolveAccessPassword(
            int status,
            String accessPassword,
            String existingPassword
    ) {
        if (status != STATUS_PASSWORD) {
            return null;
        }

        String normalizedPassword =
                normalizeOptionalText(accessPassword);

        if (normalizedPassword != null) {
            return passwordEncoder.encode(normalizedPassword);
        }

        if (existingPassword != null
                && !existingPassword.isBlank()) {
            return existingPassword;
        }

        throw new BizException(
                40000,
                "密码访问文章必须提供访问密码"
        );
    }

    /**
     * 处理发布时间
     */
    private LocalDateTime resolvePublishedAt(
            Article existingArticle,
            int status,
            LocalDateTime now
    ) {
        if (status == STATUS_DRAFT) {
            return existingArticle.getPublishedAt();
        }

        if (existingArticle.getPublishedAt() == null) {
            return now;
        }

        return existingArticle.getPublishedAt();
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