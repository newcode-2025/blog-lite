package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.AdminArticleCreateRequest;
import com.example.bloglite.dto.AdminArticleCreateResultDTO;
import com.example.bloglite.dto.AdminArticleDetailDTO;
import com.example.bloglite.dto.AdminArticleListItemDTO;
import com.example.bloglite.dto.AdminArticleUpdateRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.Category;
import com.example.bloglite.entity.Tag;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.AdminArticleMapper;
import com.example.bloglite.mapper.ArticleTagMapper;
import com.example.bloglite.mapper.CategoryMapper;
import com.example.bloglite.mapper.TagMapper;
import com.example.bloglite.service.AdminArticleService;
import com.example.bloglite.util.UserContext;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

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
    private final ArticleTagMapper articleTagMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminArticleServiceImpl(
            AdminArticleMapper adminArticleMapper,
            ArticleTagMapper articleTagMapper,
            CategoryMapper categoryMapper,
            TagMapper tagMapper,
            PasswordEncoder passwordEncoder
    ) {
        this.adminArticleMapper = adminArticleMapper;
        this.articleTagMapper = articleTagMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.passwordEncoder = passwordEncoder;
    }

    /**
     * 分页查询当前登录用户的文章
     */
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

        UserDTO currentUser = getCurrentUser();

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
     * 新建当前登录用户的文章
     */
    @Transactional
    @Override
    public AdminArticleCreateResultDTO createArticle(
            AdminArticleCreateRequest request
    ) {
        UserDTO currentUser = getCurrentUser();

        List<Long> tagIds = normalizeTagIds(request.getTagIds());

        validateCategory(request.getCategoryId());
        validateTagsExist(tagIds);

        int status = normalizeArticleStatus(request.getStatus());

        String encodedAccessPassword = resolveAccessPassword(
                status,
                request.getAccessPassword(),
                null
        );

        String title = normalizeRequiredText(
                request.getTitle(),
                "文章标题不能为空"
        );

        validateRequiredContent(request.getContent());

        LocalDateTime now = LocalDateTime.now();

        Article article = new Article();
        article.setTitle(title);
        article.setSummary(
                normalizeOptionalText(request.getSummary())
        );
        article.setContent(request.getContent());
        article.setCoverUrl(
                normalizeOptionalText(request.getCoverUrl())
        );
        article.setCategoryId(request.getCategoryId());
        article.setAuthorId(currentUser.getId());
        article.setStatus(status);
        article.setAccessPassword(encodedAccessPassword);
        article.setIsTop(0);
        article.setIsFeatured(0);
        article.setViewCount(0L);
        article.setIsDeleted(0);
        article.setPublishedAt(
                status == STATUS_DRAFT ? null : now
        );

        int articleRows = adminArticleMapper.insert(article);

        if (articleRows != 1 || article.getId() == null) {
            throw new BizException(
                    50000,
                    "文章创建失败，请稍后重试"
            );
        }

        if (!tagIds.isEmpty()) {
            int tagRows = articleTagMapper.insertBatch(
                    article.getId(),
                    tagIds
            );

            if (tagRows != tagIds.size()) {
                throw new BizException(
                        50000,
                        "文章标签保存失败，请稍后重试"
                );
            }
        }

        return new AdminArticleCreateResultDTO(
                article.getId(),
                article.getTitle(),
                article.getStatus(),
                article.getPublishedAt(),
                List.copyOf(tagIds)
        );
    }

    /**
     * 查询当前登录用户的文章编辑详情
     */
    @Override
    public AdminArticleDetailDTO getArticleDetail(Long articleId) {
        UserDTO currentUser = getCurrentUser();

        getOwnedArticle(articleId, currentUser.getId());

        AdminArticleDetailDTO detail =
                adminArticleMapper.selectAdminArticleDetail(articleId);

        if (detail == null) {
            throw new BizException(40400, "文章不存在");
        }

        return detail;
    }

    /**
     * 更新当前登录用户的文章
     */
    @Transactional
    @Override
    public AdminArticleDetailDTO updateArticle(
            Long articleId,
            AdminArticleUpdateRequest request
    ) {
        UserDTO currentUser = getCurrentUser();

        Article existingArticle = getOwnedArticle(
                articleId,
                currentUser.getId()
        );

        List<Long> tagIds = normalizeTagIds(request.getTagIds());

        validateCategory(request.getCategoryId());
        validateTagsExist(tagIds);

        int status = normalizeRequiredStatus(request.getStatus());

        String encodedAccessPassword = resolveAccessPassword(
                status,
                request.getAccessPassword(),
                existingArticle.getAccessPassword()
        );

        String title = normalizeRequiredText(
                request.getTitle(),
                "文章标题不能为空"
        );

        validateRequiredContent(request.getContent());

        LocalDateTime now = LocalDateTime.now();

        LocalDateTime publishedAt = resolvePublishedAt(
                existingArticle,
                status,
                now
        );

        LambdaUpdateWrapper<Article> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Article::getId, articleId)
                .eq(Article::getAuthorId, currentUser.getId())
                .eq(Article::getIsDeleted, 0)
                .set(
                        Article::getTitle,
                        title
                )
                .set(
                        Article::getSummary,
                        normalizeOptionalText(request.getSummary())
                )
                .set(
                        Article::getContent,
                        request.getContent()
                )
                .set(
                        Article::getCoverUrl,
                        normalizeOptionalText(request.getCoverUrl())
                )
                .set(
                        Article::getCategoryId,
                        request.getCategoryId()
                )
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

        int updatedRows = adminArticleMapper.update(
                null,
                updateWrapper
        );

        if (updatedRows < 1) {
            throw new BizException(40400, "文章不存在");
        }

        articleTagMapper.deleteByArticleId(articleId);

        if (!tagIds.isEmpty()) {
            int tagRows = articleTagMapper.insertBatch(
                    articleId,
                    tagIds
            );

            if (tagRows != tagIds.size()) {
                throw new BizException(
                        50000,
                        "文章标签同步失败，请稍后重试"
                );
            }
        }

        AdminArticleDetailDTO detail =
                adminArticleMapper.selectAdminArticleDetail(articleId);

        if (detail == null) {
            throw new BizException(40400, "文章不存在");
        }

        return detail;
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
     * 查询当前用户拥有的未删除文章
     */
    private Article getOwnedArticle(
            Long articleId,
            Long currentUserId
    ) {
        if (articleId == null || articleId < 1) {
            throw new BizException(40000, "文章 ID 不正确");
        }

        Article article = adminArticleMapper.selectById(articleId);

        if (article == null
                || Integer.valueOf(1).equals(
                article.getIsDeleted()
        )) {
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
     * 校验并规范化新建文章状态
     */
    private int normalizeArticleStatus(Integer status) {
        if (status == null) {
            return STATUS_DRAFT;
        }

        validateStatus(status);
        return status;
    }

    /**
     * 校验更新文章状态
     */
    private int normalizeRequiredStatus(Integer status) {
        if (status == null) {
            throw new BizException(
                    40000,
                    "文章状态不能为空"
            );
        }

        validateStatus(status);
        return status;
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
     * 处理访问密码
     * 新密码为空时，密码访问文章保留原密码
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
     * 草稿保留原发布时间；首次发布时写入当前时间
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
     * 规范化标签 ID
     */
    private List<Long> normalizeTagIds(List<Long> tagIds) {
        if (tagIds == null || tagIds.isEmpty()) {
            return List.of();
        }

        List<Long> normalizedTagIds =
                new ArrayList<>(tagIds.size());

        Set<Long> uniqueTagIds = new HashSet<>();

        for (Long tagId : tagIds) {
            if (tagId == null || tagId < 1) {
                throw new BizException(
                        40000,
                        "标签 ID 必须大于 0"
                );
            }

            if (!uniqueTagIds.add(tagId)) {
                throw new BizException(
                        40000,
                        "标签 ID 不能重复"
                );
            }

            normalizedTagIds.add(tagId);
        }

        return normalizedTagIds;
    }

    /**
     * 校验分类是否存在
     */
    private void validateCategory(Long categoryId) {
        if (categoryId == null) {
            return;
        }

        Category category = categoryMapper.selectById(categoryId);

        if (category == null) {
            throw new BizException(
                    40400,
                    "分类不存在"
            );
        }
    }

    /**
     * 校验标签是否全部存在
     */
    private void validateTagsExist(List<Long> tagIds) {
        if (tagIds.isEmpty()) {
            return;
        }

        List<Tag> tags = tagMapper.selectBatchIds(tagIds);

        if (tags.size() != tagIds.size()) {
            throw new BizException(
                    40400,
                    "存在不存在的标签"
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
     * 校验文章正文
     */
    private void validateRequiredContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new BizException(
                    40000,
                    "文章正文不能为空"
            );
        }
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