package com.ahut.vo;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 搬家工人端“我的评价”列表项 VO
 * 用于列表页展示，精简数据。
 */
@Data
public class MoverMyRatingListVO {

    // 评价核心标识
    private Long id; // 评价记录ID

    // 订单概览信息 (便于快速识别关联订单)
    private String orderNumber; // 订单号 (来自 moving_order 表)
    private String customerName; // 评价者姓名 (来自 customer 表)

    // 评价内容摘要
    private Integer ratingValue; // 评分值 (例如：4星)
    private String commentSnippet; // 评价内容摘要（已截断，来自 rating.comment）

    // 时间信息
    private LocalDateTime ratingTime; // 评分发生的时间

    // 内部关联ID (通常不直接展示在前端列表，但对前端逻辑或后续操作有用)
    private Long orderId;     // 关联订单ID

}
