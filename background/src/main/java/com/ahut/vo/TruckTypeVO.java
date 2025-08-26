package com.ahut.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TruckTypeVO {
    private Long id;
    private String typeName;    // 货车类型名称
    private String capacity;    // 车厢长宽高
    private String description; // 货车类型描述
    private BigDecimal baseFare; // 起步价
    private BigDecimal pricePerKmTier1; // 5-25公里每公里价格
    private BigDecimal pricePerKmTier2; // 25-30公里每公里价格
    private BigDecimal pricePerKmTier3; // 30-50公里每公里价格
    private BigDecimal pricePerKmTier4; // 50-80公里每公里价格
    private BigDecimal pricePerKmTier5; // 超过80公里每公里价格
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser; // 创建用户ID
    private Long updateUser; // 更新用户ID
    private String createUserName; // 创建管理员姓名 (通过 JOIN 获取)
    private String updateUserName; // 更新管理员姓名 (通过 JOIN 获取)
}