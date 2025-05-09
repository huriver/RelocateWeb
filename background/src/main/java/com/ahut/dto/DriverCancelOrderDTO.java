package com.***REMOVED***.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// 司机取消订单请求DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverCancelOrderDTO {

    private Long orderId; // 要取消的订单ID，与前端约定好的字段名

    private String cancelReason; // 取消原因 (可选)，对应前端传入的文本

}