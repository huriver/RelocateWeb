package com.ahut.dto;

import lombok.Data;


/**
 * 管理员取消订单请求参数DTO
 */
@Data
public class AdminOrderCancelDTO {

    private String cancelReason;

}