package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.example.bloglite.dto.SiteConfigDTO;
import com.example.bloglite.dto.SiteConfigUpdateRequest;
import com.example.bloglite.dto.SiteOverviewDTO;
import com.example.bloglite.entity.Article;
import com.example.bloglite.entity.ArticleComment;
import com.example.bloglite.entity.Talk;
import com.example.bloglite.entity.WebsiteConfig;
import com.example.bloglite.enums.ArticleCommentStatus;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.ArticleCommentMapper;
import com.example.bloglite.mapper.ArticleMapper;
import com.example.bloglite.mapper.CategoryMapper;
import com.example.bloglite.mapper.TagMapper;
import com.example.bloglite.mapper.TalkMapper;
import com.example.bloglite.mapper.WebsiteConfigMapper;
import com.example.bloglite.service.SiteService;
import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * 站点信息与网站配置服务实现
 */
@Slf4j
@Service
public class SiteServiceImpl implements SiteService {

    private static final long CONFIG_ID = 1L;

    private static final Gson JSON_GSON = new Gson();

    private static final Gson CONFIG_WRITE_GSON = new GsonBuilder()
            .addSerializationExclusionStrategy(new ExclusionStrategy() {
                @Override
                public boolean shouldSkipField(FieldAttributes field) {
                    return "version".equals(field.getName());
                }

                @Override
                public boolean shouldSkipClass(Class<?> clazz) {
                    return false;
                }
            })
            .create();

    private final WebsiteConfigMapper websiteConfigMapper;
    private final ArticleMapper articleMapper;
    private final CategoryMapper categoryMapper;
    private final TagMapper tagMapper;
    private final TalkMapper talkMapper;
    private final ArticleCommentMapper articleCommentMapper;

    public SiteServiceImpl(
            WebsiteConfigMapper websiteConfigMapper,
            ArticleMapper articleMapper,
            CategoryMapper categoryMapper,
            TagMapper tagMapper,
            TalkMapper talkMapper,
            ArticleCommentMapper articleCommentMapper
    ) {
        this.websiteConfigMapper = websiteConfigMapper;
        this.articleMapper = articleMapper;
        this.categoryMapper = categoryMapper;
        this.tagMapper = tagMapper;
        this.talkMapper = talkMapper;
        this.articleCommentMapper = articleCommentMapper;
    }

    @Override
    public SiteConfigDTO getConfig() {
        WebsiteConfig websiteConfig = ensureConfig();
        return parseConfig(websiteConfig);
    }

    @Override
    public SiteOverviewDTO getOverview() {
        long articleCount = articleMapper.countPublishedArticles();
        long categoryCount = categoryMapper.selectCount(null);
        long tagCount = tagMapper.selectCount(null);
        long talkCount = talkMapper.selectCount(
                Wrappers.<Talk>lambdaQuery()
                        .eq(Talk::getStatus, 1)
                        .eq(Talk::getIsDeleted, 0)
        );
        long commentCount = articleCommentMapper.selectCount(
                Wrappers.<ArticleComment>lambdaQuery()
                        .eq(
                                ArticleComment::getStatus,
                                ArticleCommentStatus.APPROVED.getCode()
                        )
                        .eq(ArticleComment::getIsDeleted, 0)
        );
        Long articleViewCount = articleMapper.sumPublishedArticleViews();

        return new SiteOverviewDTO(
                articleCount,
                categoryCount,
                tagCount,
                talkCount,
                commentCount,
                articleViewCount == null ? 0L : articleViewCount,
                getConfig()
        );
    }

    @Override
    public SiteConfigDTO updateConfig(SiteConfigUpdateRequest request) {
        if (request == null) {
            throw new BizException(40000, "网站配置不能为空");
        }

        if (request.getVersion() == null) {
            throw new BizException(40000, "配置版本号不能为空");
        }

        normalizeConfig(request);

        WebsiteConfig currentConfig = ensureConfig();
        String configJson = CONFIG_WRITE_GSON.toJson(request);
        int affectedRows = websiteConfigMapper.updateConfig(
                configJson,
                request.getVersion()
        );

        if (affectedRows == 0) {
            throw new BizException(
                    40900,
                    "网站配置已被其他管理员修改，请刷新后重试"
            );
        }

        WebsiteConfig updatedConfig = websiteConfigMapper.selectById(
                currentConfig.getId()
        );
        return parseConfig(updatedConfig);
    }

    private WebsiteConfig ensureConfig() {
        WebsiteConfig websiteConfig = websiteConfigMapper.selectById(
                CONFIG_ID
        );

        if (websiteConfig != null) {
            return websiteConfig;
        }

        WebsiteConfig defaultConfig = new WebsiteConfig();
        defaultConfig.setId(CONFIG_ID);
        defaultConfig.setConfigJson(
                CONFIG_WRITE_GSON.toJson(createDefaultConfig())
        );
        defaultConfig.setVersion(0);

        try {
            websiteConfigMapper.insert(defaultConfig);
        } catch (DuplicateKeyException exception) {
            log.debug("网站配置已由其他线程初始化");
        }

        WebsiteConfig createdConfig = websiteConfigMapper.selectById(
                CONFIG_ID
        );

        if (createdConfig == null) {
            throw new BizException(50000, "网站配置初始化失败");
        }

        return createdConfig;
    }

    private SiteConfigDTO parseConfig(WebsiteConfig websiteConfig) {
        SiteConfigDTO config;

        try {
            config = JSON_GSON.fromJson(
                    websiteConfig.getConfigJson(),
                    SiteConfigDTO.class
            );
        } catch (JsonSyntaxException exception) {
            log.error("网站配置 JSON 格式错误，已返回默认配置", exception);
            config = createDefaultConfig();
        }

        if (config == null) {
            config = createDefaultConfig();
        }

        applyDefaults(config);
        config.setVersion(websiteConfig.getVersion());
        return config;
    }

    private SiteConfigDTO createDefaultConfig() {
        SiteConfigDTO config = new SiteConfigDTO();
        config.setSiteName("blog-lite");
        config.setSiteDescription("");
        config.setAuthorName("");
        config.setAuthorAvatar("");
        config.setAuthorIntro("");
        config.setLogo("");
        config.setNotice("");
        config.setWebsiteCreateTime("");
        config.setIcpNumber("");
        config.setGithub("");
        config.setGitee("");
        config.setQq("");
        config.setWeChat("");
        config.setWeibo("");
        config.setCsdn("");
        config.setZhihu("");
        config.setJuejin("");
        config.setTwitter("");
        config.setStackOverflow("");
        config.setEmail("");
        config.setCommentReviewEnabled(1);
        config.setEmailNotificationEnabled(0);
        config.setVersion(0);
        return config;
    }

    private void applyDefaults(SiteConfigDTO config) {
        if (config.getSiteName() == null || config.getSiteName().isBlank()) {
            config.setSiteName("blog-lite");
        }
        config.setSiteDescription(defaultString(config.getSiteDescription()));
        config.setAuthorName(defaultString(config.getAuthorName()));
        config.setAuthorAvatar(defaultString(config.getAuthorAvatar()));
        config.setAuthorIntro(defaultString(config.getAuthorIntro()));
        config.setLogo(defaultString(config.getLogo()));
        config.setNotice(defaultString(config.getNotice()));
        config.setWebsiteCreateTime(
                defaultString(config.getWebsiteCreateTime())
        );
        config.setIcpNumber(defaultString(config.getIcpNumber()));
        config.setGithub(defaultString(config.getGithub()));
        config.setGitee(defaultString(config.getGitee()));
        config.setQq(defaultString(config.getQq()));
        config.setWeChat(defaultString(config.getWeChat()));
        config.setWeibo(defaultString(config.getWeibo()));
        config.setCsdn(defaultString(config.getCsdn()));
        config.setZhihu(defaultString(config.getZhihu()));
        config.setJuejin(defaultString(config.getJuejin()));
        config.setTwitter(defaultString(config.getTwitter()));
        config.setStackOverflow(defaultString(config.getStackOverflow()));
        config.setEmail(defaultString(config.getEmail()));

        if (config.getCommentReviewEnabled() == null) {
            config.setCommentReviewEnabled(1);
        }
        if (config.getEmailNotificationEnabled() == null) {
            config.setEmailNotificationEnabled(0);
        }
    }

    private void normalizeConfig(SiteConfigDTO config) {
        config.setSiteName(trim(config.getSiteName()));
        config.setSiteDescription(trim(config.getSiteDescription()));
        config.setAuthorName(trim(config.getAuthorName()));
        config.setAuthorAvatar(trim(config.getAuthorAvatar()));
        config.setAuthorIntro(trim(config.getAuthorIntro()));
        config.setLogo(trim(config.getLogo()));
        config.setNotice(trim(config.getNotice()));
        config.setWebsiteCreateTime(trim(config.getWebsiteCreateTime()));
        config.setIcpNumber(trim(config.getIcpNumber()));
        config.setGithub(trim(config.getGithub()));
        config.setGitee(trim(config.getGitee()));
        config.setQq(trim(config.getQq()));
        config.setWeChat(trim(config.getWeChat()));
        config.setWeibo(trim(config.getWeibo()));
        config.setCsdn(trim(config.getCsdn()));
        config.setZhihu(trim(config.getZhihu()));
        config.setJuejin(trim(config.getJuejin()));
        config.setTwitter(trim(config.getTwitter()));
        config.setStackOverflow(trim(config.getStackOverflow()));
        config.setEmail(trim(config.getEmail()));
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }

    private String defaultString(String value) {
        return value == null ? "" : value;
    }
}