package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.ExceptionLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统异常日志数据访问接口
 */
@Mapper
public interface ExceptionLogMapper extends BaseMapper<ExceptionLog> {
}
