package com.ahut.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 订单支付返回结果VO (模拟)
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderPaymentVO {
    private Integer payStatus; // 模拟支付状态 (例如，直接返回 PaymentStatusConstant.PAID)
    private String orderNumber; // 返回对应的订单号
}