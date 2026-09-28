package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.dto.CategoryDTO;
import com.example.bloglite.entity.Category;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface CategoryMapper extends BaseMapper<Category> {

    List<CategoryDTO> selectCategories();
}
