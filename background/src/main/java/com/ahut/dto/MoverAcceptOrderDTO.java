package com.***REMOVED***.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 搬家工人接单请求参数 DTO
 * 包含工人希望接受的订单ID。
 */
@Data
public class MoverAcceptOrderDTO implements Serializable {
    private Long orderId; // 订单ID，工人希望接受的订单的唯一标识
}