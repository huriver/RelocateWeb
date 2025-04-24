package com.***REMOVED***.vo;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
public class MovingTipsVO {
    private Long id;
    private String title; // 须知标题
    private String content; // 须知内容
    private String category; // 须知分类
    private LocalDate publishDate; // 发布日期
    private Boolean isPublished; // 是否已发布
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser; // 创建用户ID
    private Long updateUser; // 更新用户ID

    // ====== 关联管理员表的姓名字段 (通过 JOIN 获取) ======
    private String createUserName; // 创建管理员姓名
    private String updateUserName; // 更新管理员姓名
}