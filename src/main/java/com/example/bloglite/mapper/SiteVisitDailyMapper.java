package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.SiteVisitDaily;
import org.apache.ibatis.annotations.Mapper;

/**
 * 网站每日访问汇总数据访问接口
 */
@Mapper
public interface SiteVisitDailyMapper extends BaseMapper<SiteVisitDaily> {
}
