package com.***REMOVED***.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class TruckTypePageQueryDTO {
    private int page;                                   // 页码
    private int pageSize;                               // 每页记录数
    private String typeName;                            // 货车类型名称搜索
    private BigDecimal minBaseFare;                     // 起步价范围 - 最低
    private BigDecimal maxBaseFare;                     // 起步价范围 - 最高
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart;              // 创建时间范围开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd;                // 创建时间范围结束
}