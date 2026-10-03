package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.ApiResource;
import org.apache.ibatis.annotations.Mapper;

/**
 * 接口资源数据访问接口
 */
@Mapper
public interface ApiResourceMapper extends BaseMapper<ApiResource> {
}
