package com.***REMOVED***.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class ServiceCategoryPageQueryDTO {
    private int page;
    private int pageSize;
    private String typeName;                // 服务类型名称搜索
    private BigDecimal minPriceMultiplier;  // 价格乘数范围 - 最低
    private BigDecimal maxPriceMultiplier;  // 价格乘数范围 - 最高
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;  // 创建时间范围开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;    // 创建时间范围结束
}