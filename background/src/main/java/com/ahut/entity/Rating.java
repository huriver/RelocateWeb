package com.ahut.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Rating {
    private Long id;
    private Long orderId;              // 订单ID
    private Long customerId;           // 消费者ID
    private Long rateeId;              // 被评分者ID
    private String ratingType;         // 评分类型
    private Integer ratingValue;       // 评分值 (根据你的表结构是 Integer)
    private String comment;            // 评价内容
    private LocalDateTime ratingTime;  // 评分时间
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}