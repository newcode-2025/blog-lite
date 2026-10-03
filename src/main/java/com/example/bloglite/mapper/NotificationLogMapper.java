package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.NotificationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 邮件通知记录数据访问接口
 */
@Mapper
public interface NotificationLogMapper extends BaseMapper<NotificationLog> {
}
