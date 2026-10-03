package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.Menu;
import org.apache.ibatis.annotations.Mapper;

/**
 * 后台菜单数据访问接口
 */
@Mapper
public interface MenuMapper extends BaseMapper<Menu> {
}
