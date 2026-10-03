package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 可选定时任务处理器
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TaskHandlerOptionDTO {

    private String taskKey;

    private String taskName;
}