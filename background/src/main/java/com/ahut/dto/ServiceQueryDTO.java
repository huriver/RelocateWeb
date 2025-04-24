package com.***REMOVED***.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceQueryDTO {
    private int page;
    private int pageSize;
    private String serviceName; // 服务项名称搜索
    private Long categoryId; // 服务类型ID
    private Long truckTypeId; // 货车类型ID

    // 绩效范围筛选
    private BigDecimal minAverageRating; // 平均评分范围 - 最低
    private BigDecimal maxAverageRating; // 平均评分范围 - 最高
    private Integer minRatingCount; // 评分数量范围 - 最低
    private Integer maxRatingCount; // 评分数量范围 - 最高

    // 创建时间范围筛选
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart; // 创建时间范围开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd; // 创建时间范围结束
}