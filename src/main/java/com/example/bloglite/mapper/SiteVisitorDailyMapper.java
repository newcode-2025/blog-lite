package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.SiteVisitorDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网站每日访客去重数据访问接口
 */
@Mapper
public interface SiteVisitorDailyMapper extends BaseMapper<SiteVisitorDaily> {
}
