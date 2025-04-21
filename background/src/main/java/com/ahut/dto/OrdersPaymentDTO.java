package com.***REMOVED***.dto;

import lombok.Data;

/**
 * 订单支付请求参数DTO
 */
@Data
public class OrdersPaymentDTO {
    private String orderNumber; // 要支付的订单号
    private Integer payMethod;  // 选择的支付方式 (1微信, 2支付宝, 3云闪付等)
}