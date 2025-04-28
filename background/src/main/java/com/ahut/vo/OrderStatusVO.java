package com.***REMOVED***.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 订单状态视图对象
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusVO {
    private Integer code; // 状态码，对应 OrderStatusConstant 的整数值
    private String description; // 状态描述，对应 OrderStatusConstant 的文字描述
}