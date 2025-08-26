package com.ahut.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * 搬家工人取消订单分配请求参数 DTO
 */
@Data
public class MoverCancelOrderDTO implements Serializable {

    private Long orderId; // 要取消的订单ID，与前端约定好的字段名

    private String cancelReason; // 取消原因 (可选)，对应前端传入的文本

}