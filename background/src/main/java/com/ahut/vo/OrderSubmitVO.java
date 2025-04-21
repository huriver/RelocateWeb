package com.***REMOVED***.vo;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 用户提交订单返回结果VO
 */
@Data
@Builder
public class OrderSubmitVO {
    private Long id; // 新创建订单的ID
    private String orderNumber; // 新创建订单的订单号
    private BigDecimal orderAmount; // 订单的总金额
    private LocalDateTime orderTime; // 订单的创建时间
}