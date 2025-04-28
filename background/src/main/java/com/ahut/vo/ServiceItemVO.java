package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 管理员分页查询服务项VO
 */
@Data
public class ServiceItemVO {
    private Long id;
    private Long categoryId; // 服务类型ID
    private Long truckTypeId; // 货车类型ID
    private String serviceName; // 服务项名称
    private String shortDescription; // 服务项简短描述
    private String loadingCapacityDescription; // 装载能力详细说明
    private BigDecimal averageRating; // 平均评分值
    private Integer ratingCount; // 评分数量
    private Integer status; // 服务项状态：0-停售，1-起售
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser; // 创建用户ID
    private Long updateUser; // 更新用户ID

    // ====== 关联表的名称字段 (通过 JOIN 获取) ======
    private String categoryName; // 所属服务类型名称
    private String truckTypeName; // 关联货车类型名称
    private String createUserName; // 创建管理员姓名
    private String updateUserName; // 更新管理员姓名
}