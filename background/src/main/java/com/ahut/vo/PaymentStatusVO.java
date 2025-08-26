package com.ahut.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 支付状态视图对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentStatusVO {
    private Integer code; // 状态码，对应 PaymentStatusConstant 的整数值
    private String description; // 状态描述，对应 PaymentStatusConstant 的文字描述
}