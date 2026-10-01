package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.UserAccount;
import org.apache.ibatis.annotations.Mapper;

/**
 * 用户账号数据访问接口
 */
@Mapper
public interface UserAccountMapper extends BaseMapper<UserAccount> {
}
