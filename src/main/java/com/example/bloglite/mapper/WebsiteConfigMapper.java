package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.WebsiteConfig;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网站配置数据访问接口
 */
@Mapper
public interface WebsiteConfigMapper extends BaseMapper<WebsiteConfig> {
}
