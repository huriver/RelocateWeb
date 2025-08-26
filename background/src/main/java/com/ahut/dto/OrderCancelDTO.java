package com.ahut.dto;

import lombok.Data;

/**
 * 用户取消订单请求参数DTO
 */
@Data
public class OrderCancelDTO {
    private String cancelReason; // 取消原因
}