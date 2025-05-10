package com.***REMOVED***.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 搬家工人待接订单分页查询DTO
 */
@Data
public class MoverAvailableOrderPageQueryDTO {

    private Integer page;       // 页码
    private Integer pageSize;   // 每页记录数

    // 按预约时间范围筛选
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate startDate; // 预约开始日期 (包含)

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;   // 预约结束日期 (包含)

    // 按所需搬运工人数筛选 (例如，只看需要1人的订单)
    private Integer numberOfHelpers;

    // 按服务类别筛选
    private Long serviceCategoryId;

}