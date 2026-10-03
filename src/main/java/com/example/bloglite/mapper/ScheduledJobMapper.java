package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.ScheduledJob;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务定义数据访问接口
 */
@Mapper
public interface ScheduledJobMapper extends BaseMapper<ScheduledJob> {
}
