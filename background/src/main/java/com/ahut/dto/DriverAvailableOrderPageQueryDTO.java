package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Data
public class DriverAvailableOrderPageQueryDTO {

    private Integer page;       // 页码
    private Integer pageSize;   // 每页记录数

    // 按预约时间范围筛选
    @DateTimeFormat(pattern = "yyyy-MM-dd") // 告诉 Spring 如何格式化日期字符串
    private LocalDate startDate; // 预约开始日期 (包含)

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;   // 预约结束日期 (包含)

    // 按特定可驾驭货车类型筛选
    private Long truckTypeId;

    // 按服务类别筛选
    private Long serviceCategoryId;

}