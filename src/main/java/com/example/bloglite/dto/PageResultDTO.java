package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 通用分页返回结果
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResultDTO<T> {

    private List<T> records;

    private Long count;

    private Long current;

    private Long size;
}
