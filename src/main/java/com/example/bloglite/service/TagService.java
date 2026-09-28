package com.example.bloglite.service;

import com.example.bloglite.dto.TagListItemDTO;

import java.util.List;

/**
 * 标签业务接口
 */
public interface TagService {


    List<TagListItemDTO> listTags();
}