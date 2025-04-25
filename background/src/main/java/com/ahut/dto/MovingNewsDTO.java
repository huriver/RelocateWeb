package com.***REMOVED***.dto;

import lombok.Data;

import java.time.LocalDate;

@Data
public class MovingNewsDTO {
    private Long id; // 统一DTO包含ID字段
    private String title; // 新闻标题
    private String content; // 新闻内容
    private LocalDate publishDate; // 发布日期
    private Boolean isPublished; // 是否已发布 (可选)
}