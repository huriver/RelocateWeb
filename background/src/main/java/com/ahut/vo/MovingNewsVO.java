package com.***REMOVED***.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MovingNewsVO {
    private Long id;
    private String title; // 新闻标题
    private String content; // 新闻内容
    private LocalDate publishDate; // 发布日期 (根据数据库类型，如果真是 datetime 映射到 LocalDateTime)
    private Boolean isPublished; // 是否已发布
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser; // 创建用户ID
    private Long updateUser; // 更新用户ID

    // ====== 关联管理员表的姓名字段 (通过 JOIN 获取) ======
    private String createUserName; // 创建管理员姓名
    private String updateUserName; // 更新管理员姓名
}