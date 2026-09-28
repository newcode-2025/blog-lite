package com.example.bloglite.service.impl;

import com.example.bloglite.dto.TagListItemDTO;
import com.example.bloglite.mapper.TagMapper;
import com.example.bloglite.service.TagService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 标签业务实现
 */
@Service
public class TagServiceImpl implements TagService {

    private final TagMapper tagMapper;

    public TagServiceImpl(TagMapper tagMapper) {
        this.tagMapper = tagMapper;
    }

    @Override
    public List<TagListItemDTO> listTags() {
        return tagMapper.selectTagList();
    }
}