package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;


@Data
public class MovingTipsDTO {
    private Long id;

    private String title; // 须知标题

    private String content; // 须知内容

    private String category; // 须知分类 (可选)

    @DateTimeFormat(pattern = "yyyy-MM-dd") // 匹配数据库 date 类型和前端日期输入
    private LocalDate publishDate; // 发布日期
}