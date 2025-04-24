package com.***REMOVED***.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class MovingNewsPageQueryDTO {
    private int page; // 页码
    private int pageSize; // 每页记录数
    private String title; // 新闻标题搜索 (模糊)
    private String content; // 新闻内容关键词搜索 (模糊)
    private Boolean isPublished; // 发布状态筛选 (true/false)

    // 发布日期范围筛选 (使用 LocalDate 匹配 date 类型)
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDateStart; // 发布日期范围开始
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate publishDateEnd; // 发布日期范围结束
}