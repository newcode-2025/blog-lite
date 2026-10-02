package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 友情链接实体
 */
@Data
@TableName("friend_link")
public class FriendLink {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private String avatarUrl;

    private String address;

    private String description;

    private Integer sortOrder;

    /**
     * 1 显示，0 隐藏
     */
    private Integer status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}