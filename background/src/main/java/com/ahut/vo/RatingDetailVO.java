package com.ahut.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评分详情 VO
 */
@Data
public class RatingDetailVO {

    private Long id;             // 评分记录ID

    // --- 评分核心内容 ---
    private Integer ratingValue; // 评分值 (最直观的评价结果)
    private String comment;      // 评价内容 (完整内容)
    private String ratingType;   // 评分类型 (评价的是什么)
    private LocalDateTime ratingTime; // 评分发生的时间 (评价发生的时间点)

    // --- 关联对象关键基本信息（姓名/订单号）---
    private String orderNumber; // 关联订单号
    private String customerName; // 消费者姓名
    private String rateeName;    // 被评分者姓名

    // --- 关联对象的原始 ID ---
    private Long orderId;     // 关联订单ID
    private Long customerId;  // 消费者ID
    private Long rateeId;     // 被评分者ID

    // --- 评分记录自身的审计时间字段 ---
    private LocalDateTime createTime; // 评分记录创建时间
    private LocalDateTime updateTime; // 评分记录最后修改时间

}