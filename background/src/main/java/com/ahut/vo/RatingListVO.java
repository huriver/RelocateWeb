package com.***REMOVED***.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 评分列表项 VO (包含关联对象的关键信息)
 */
@Data
public class RatingListVO {

    private Long id; // 评分记录ID

    private Long orderId;     // 关联订单ID
    private String orderNumber; // 关联订单号 (来自 order 表)

    private Long customerId;  // 评分的消费者ID
    private String customerName; // 评分的消费者姓名 (来自 customer 表)

    private Long rateeId;     // 被评分者ID
    private String rateeName; // 被评分者姓名 (来自 driver/mover/service 表，取决于 ratingType)

    private String ratingType; // 评分类型
    private Integer ratingValue; // 评分值
    private String comment;   // 评价内容 (列表页可能截断)
    private LocalDateTime ratingTime; // 评分发生的时间

}