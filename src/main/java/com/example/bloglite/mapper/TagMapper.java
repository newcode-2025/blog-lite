package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.TagListItemDTO;
import com.example.bloglite.entity.Tag;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 标签数据访问接口
 */
@Mapper
public interface TagMapper extends BaseMapper<Tag> {


    List<TagListItemDTO> selectTagList();
}