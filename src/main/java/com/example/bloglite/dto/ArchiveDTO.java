package com.example.bloglite.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 文章归档数据
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ArchiveDTO {


    private String year;

    private String month;

    private List<ArticleCardDTO> articles;
}