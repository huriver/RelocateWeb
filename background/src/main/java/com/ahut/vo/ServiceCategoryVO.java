package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceCategoryVO {
    private Long id;
    private String typeName;
    private String description;
    private BigDecimal priceMultiplier;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser;            // 原始 ID
    private Long updateUser;            // 原始 ID
    private String createUserName;      // 创建管理员姓名
    private String updateUserName;      // 更新管理员姓名
}