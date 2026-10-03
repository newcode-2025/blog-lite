package com.example.bloglite.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.bloglite.entity.WebsiteConfig;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

/**
 * 网站配置数据访问接口
 */
@Mapper
public interface WebsiteConfigMapper extends BaseMapper<WebsiteConfig> {

    /**
     * 按版本号更新网站配置，避免并发覆盖
     */
    @Update("""
            UPDATE website_config
            SET config_json = #{configJson},
                version = version + 1,
                updated_at = CURRENT_TIMESTAMP
            WHERE id = 1
              AND version = #{version}
            """)
    int updateConfig(
            @Param("configJson") String configJson,
            @Param("version") Integer version
    );
}