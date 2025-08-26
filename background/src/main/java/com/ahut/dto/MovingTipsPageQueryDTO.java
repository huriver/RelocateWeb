package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class MovingTipsPageQueryDTO {
    private int page; // 页码
    private int pageSize; // 每页记录数
    private String title; // 须知标题搜索
    private String content; // 须知内容关键词搜索
    private String category; // 须知分类搜索/筛选
    private Boolean isPublished; // 发布状态筛选

    // 发布日期范围筛选 (使用 LocalDate 匹配 date 类型)
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 添加日期格式化注解
    private LocalDate publishDateStart; // 发布日期范围开始
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 添加日期格式化注解
    private LocalDate publishDateEnd; // 发布日期范围结束
}
