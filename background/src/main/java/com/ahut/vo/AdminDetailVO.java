package com.ahut.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 管理员详情 VO (不包含密码)
 */
@Data
public class AdminDetailVO {
    private Long id;             // 账号ID
    private String username;     // 用户名
    private String name;         // 管理员姓名
    private String photoUrl;     // 照片 URL
    private Boolean isBanned;    // 是否被禁用状态
    private LocalDateTime createTime; // 账号创建时间
    private LocalDateTime updateTime; // 账号最后修改时间
    private Long createUser;     // 创建者用户ID (数据库字段)
    private Long updateUser;     // 更新者用户ID (数据库字段)

    private String createUserName; // 新增：创建者用户名或姓名
    private String updateUserName; // 新增：更新者用户名或姓名
}