package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DriverMyOrderPageQueryDTO {

    private Integer page;       // 页码
    private Integer pageSize;   // 每页记录数

    // 可选筛选条件
    private Integer orderStatus; // 按订单状态 (1, 2, 3)

    @DateTimeFormat(pattern = "yyyy-MM-dd") // 按预约时间范围
    private LocalDate startDate;
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;

    private String orderNumber; // 按订单号

}