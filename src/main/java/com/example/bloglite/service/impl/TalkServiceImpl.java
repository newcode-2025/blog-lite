package com.example.bloglite.service.impl;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.example.bloglite.dto.AdminTalkSaveRequest;
import com.example.bloglite.dto.PageResultDTO;
import com.example.bloglite.dto.TalkDTO;
import com.example.bloglite.dto.UserDTO;
import com.example.bloglite.entity.Talk;
import com.example.bloglite.exception.BizException;
import com.example.bloglite.mapper.TalkMapper;
import com.example.bloglite.service.TalkService;
import com.example.bloglite.util.UserContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * 说说业务实现
 */
@Service
public class TalkServiceImpl implements TalkService {

    private static final int STATUS_PUBLIC = 1;
    private static final int STATUS_PRIVATE = 2;

    private static final int NOT_DELETED = 0;
    private static final int DELETED = 1;

    private static final int MAX_IMAGE_COUNT = 9;
    private static final int MAX_IMAGE_URL_LENGTH = 500;

    private static final long DEFAULT_SIZE = 10;
    private static final long MAX_SIZE = 50;

    private final TalkMapper talkMapper;

    public TalkServiceImpl(TalkMapper talkMapper) {
        this.talkMapper = talkMapper;
    }

    /**
     * 分页查询公开说说
     */
    @Override
    public PageResultDTO<TalkDTO> listPublicTalks(
            long current,
            long size
    ) {
        current = normalizeCurrent(current);
        size = normalizeSize(size);

        long offset = (current - 1) * size;

        List<TalkDTO> records =
                talkMapper.selectPublicTalks(offset, size);

        long count = talkMapper.countPublicTalks();

        return new PageResultDTO<>(
                records,
                count,
                current,
                size
        );
    }

    /**
     * 后台分页查询说说
     */
    @Override
    public PageResultDTO<TalkDTO> listAdminTalks(
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

        List<TalkDTO> records = talkMapper.selectAdminTalks(
                status,
                normalizedKeywords,
                offset,
                size
        );

        long count = talkMapper.countAdminTalks(
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
     * 新建说说
     */
    @Transactional
    @Override
    public TalkDTO createTalk(AdminTalkSaveRequest request) {
        UserDTO currentUser = UserContext.getCurrentUser();

        int status = request.getStatus() == null
                ? STATUS_PUBLIC
                : normalizeStatus(request.getStatus());

        int isTop = request.getIsTop() == null
                ? 0
                : normalizeIsTop(request.getIsTop());

        LocalDateTime now = LocalDateTime.now();

        Talk talk = new Talk();
        talk.setUserId(currentUser.getId());
        talk.setContent(normalizeContent(request.getContent()));
        talk.setImages(normalizeImages(request.getImages()));
        talk.setIsTop(isTop);
        talk.setStatus(status);
        talk.setIsDeleted(NOT_DELETED);
        talk.setCreatedAt(now);
        talk.setUpdatedAt(now);

        int insertedRows = talkMapper.insert(talk);

        if (insertedRows != 1 || talk.getId() == null) {
            throw new BizException(
                    50000,
                    "说说创建失败，请稍后重试"
            );
        }

        return loadTalk(talk.getId());
    }

    /**
     * 编辑说说
     */
    @Transactional
    @Override
    public TalkDTO updateTalk(
            Long talkId,
            AdminTalkSaveRequest request
    ) {
        Talk existingTalk = getTalkOrThrow(talkId);

        int status = request.getStatus() == null
                ? existingTalk.getStatus()
                : normalizeStatus(request.getStatus());

        int isTop = request.getIsTop() == null
                ? existingTalk.getIsTop()
                : normalizeIsTop(request.getIsTop());

        List<String> images = request.getImages() == null
                ? existingTalk.getImages()
                : normalizeImages(request.getImages());

        Talk updateTalk = new Talk();
        updateTalk.setId(talkId);
        updateTalk.setContent(
                normalizeContent(request.getContent())
        );
        updateTalk.setImages(images);
        updateTalk.setIsTop(isTop);
        updateTalk.setStatus(status);
        updateTalk.setUpdatedAt(LocalDateTime.now());

        int updatedRows = talkMapper.updateById(updateTalk);

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "说说更新失败，请稍后重试"
            );
        }

        return loadTalk(talkId);
    }

    /**
     * 软删除说说
     */
    @Transactional
    @Override
    public void deleteTalk(Long talkId) {
        Talk talk = getTalkOrThrow(talkId);

        if (Integer.valueOf(DELETED).equals(talk.getIsDeleted())) {
            return;
        }

        LambdaUpdateWrapper<Talk> updateWrapper =
                new LambdaUpdateWrapper<>();

        updateWrapper
                .eq(Talk::getId, talkId)
                .eq(Talk::getIsDeleted, NOT_DELETED)
                .set(Talk::getIsDeleted, DELETED)
                .set(Talk::getUpdatedAt, LocalDateTime.now());

        int updatedRows = talkMapper.update(null, updateWrapper);

        if (updatedRows < 1) {
            throw new BizException(
                    50000,
                    "说说删除失败，请稍后重试"
            );
        }
    }

    /**
     * 查询未删除的说说
     */
    private Talk getTalkOrThrow(Long talkId) {
        if (talkId == null || talkId < 1) {
            throw new BizException(40000, "说说 ID 不正确");
        }

        Talk talk = talkMapper.selectById(talkId);

        if (talk == null
                || Integer.valueOf(DELETED)
                .equals(talk.getIsDeleted())) {
            throw new BizException(40400, "说说不存在");
        }

        return talk;
    }

    /**
     * 重新查询后台说说数据
     */
    private TalkDTO loadTalk(Long talkId) {
        TalkDTO talk = talkMapper.selectAdminTalkById(talkId);

        if (talk == null) {
            throw new BizException(
                    50000,
                    "说说数据读取失败，请稍后重试"
            );
        }

        return talk;
    }

    /**
     * 校验列表状态
     */
    private void validateStatus(Integer status) {
        if (status == null) {
            return;
        }

        normalizeStatus(status);
    }

    /**
     * 校验并返回说说状态
     */
    private int normalizeStatus(Integer status) {
        if (status == null
                || (status != STATUS_PUBLIC
                && status != STATUS_PRIVATE)) {
            throw new BizException(
                    40000,
                    "说说状态只能是 1 或 2"
            );
        }

        return status;
    }

    /**
     * 校验并返回置顶状态
     */
    private int normalizeIsTop(Integer isTop) {
        if (isTop == null || (isTop != 0 && isTop != 1)) {
            throw new BizException(
                    40000,
                    "置顶状态只能是 0 或 1"
            );
        }

        return isTop;
    }

    /**
     * 校验说说正文
     */
    private String normalizeContent(String content) {
        if (content == null || content.trim().isEmpty()) {
            throw new BizException(
                    40000,
                    "说说内容不能为空"
            );
        }

        String normalizedContent = content.trim();

        if (normalizedContent.length() > 2000) {
            throw new BizException(
                    40000,
                    "说说内容不能超过 2000 个字符"
            );
        }

        return normalizedContent;
    }

    /**
     * 校验并规范化图片地址
     */
    private List<String> normalizeImages(List<String> images) {
        if (images == null || images.isEmpty()) {
            return List.of();
        }

        if (images.size() > MAX_IMAGE_COUNT) {
            throw new BizException(
                    40000,
                    "一条说说最多包含 9 张图片"
            );
        }

        List<String> normalizedImages =
                new ArrayList<>(images.size());

        Set<String> uniqueImages = new HashSet<>();

        for (String image : images) {
            if (image == null || image.trim().isEmpty()) {
                throw new BizException(
                        40000,
                        "图片地址不能为空"
                );
            }

            String normalizedImage = image.trim();

            if (normalizedImage.length()
                    > MAX_IMAGE_URL_LENGTH) {
                throw new BizException(
                        40000,
                        "图片地址不能超过 500 个字符"
                );
            }

            if (!uniqueImages.add(normalizedImage)) {
                throw new BizException(
                        40000,
                        "图片地址不能重复"
                );
            }

            normalizedImages.add(normalizedImage);
        }

        return normalizedImages;
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