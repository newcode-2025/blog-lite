package com.example.bloglite.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("tag")
public class Tag {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String name;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}
