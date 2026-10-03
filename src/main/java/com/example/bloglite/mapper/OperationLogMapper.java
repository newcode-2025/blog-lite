package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.OperationLog;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台操作日志数据访问接口
 */
@Mapper
public interface OperationLogMapper extends BaseMapper<OperationLog> {
}
