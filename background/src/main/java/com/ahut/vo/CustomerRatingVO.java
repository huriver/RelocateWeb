package com.***REMOVED***.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CustomerRatingVO {
    private String orderNumber;        // 订单号
    private String orderServiceName;   // 订单服务项名称
    private String ratedEntityType;    // 评价对象类型 (DRIVER, MOVER, SERVICE)
    private String ratedEntityName;    // 被评价对象名称 (司机姓名, 搬运工姓名, 或服务项名称)
    private Integer ratingValue;       // 评分值
    private String comment;            // 评价内容
    private LocalDateTime ratingTime;  // 评分时间
}