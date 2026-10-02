package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.bloglite.typehandler.TalkImageListTypeHandler;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 说说实体
 */
@Data
@TableName(value = "talk", autoResultMap = true)
public class Talk {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;

    private String content;

    /**
     * 图片 URL 列表，数据库中使用 JSON 保存
     */
    @TableField(
            value = "images",
            typeHandler = TalkImageListTypeHandler.class
    )
    private List<String> images;

    private Integer isTop;

    /**
     * 1 公开，2 私密
     */
    private Integer status;

    private Integer isDeleted;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}