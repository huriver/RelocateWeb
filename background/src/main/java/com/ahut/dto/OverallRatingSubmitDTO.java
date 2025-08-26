package com.ahut.dto;

import lombok.Data;

import java.util.List;


/**
 * 用户提交订单整体评价请求参数DTO
 */
@Data
public class OverallRatingSubmitDTO {
    private Long orderId;                       // 订单ID
    private List<SingleRatingDTO> ratings;      // 包含多个具体的评分项，例如对司机、搬运工人、服务的评分
}