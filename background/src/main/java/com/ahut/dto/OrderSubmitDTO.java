package com.ahut.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户提交订单请求参数DTO
 */
@Data
public class OrderSubmitDTO {
    private Long serviceId; // 用户选择的服务项ID
    private LocalDateTime reservationTime; // 预约时间
    private String movingOrigin; // 搬家起点详细地址
    private String movingDestination; // 搬家目的地详细地址
    private Integer numberOfHelpers; // 用户选择的搬运工人数量
    private String notes; // 备注
}