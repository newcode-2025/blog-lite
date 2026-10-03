package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.ScheduledJobLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 定时任务执行日志数据访问接口
 */
@Mapper
public interface ScheduledJobLogMapper extends BaseMapper<ScheduledJobLog> {
}
