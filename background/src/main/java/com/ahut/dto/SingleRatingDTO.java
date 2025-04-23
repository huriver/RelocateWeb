package com.***REMOVED***.dto;

import lombok.Data;

/**
 * 单个评分项DTO
 */
@Data
public class SingleRatingDTO {
    // 不需要 orderId 和 customerId，它们在 OverallRatingSubmitDTO 中
    private Long rateeId;        // 被评分者ID (司机ID, 搬运工人ID, 或服务的某种标识)
    private String ratingType;   // 评分类型（例如：DRIVER，MOVER，SERVICE）
    private Integer ratingValue; // 评分值（例如：1-5星）
    private String comment;      // 评价内容 (可选)
}