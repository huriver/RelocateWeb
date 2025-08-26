package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

/**
 * 搬家工人端 - 我的订单分页查询条件 DTO
 */
@Data
public class MoverMyOrderPageQueryDTO {

    private Integer page;       // 页码
    private Integer pageSize;   // 每页记录数

    // --- 可选筛选条件 ---
    private Integer orderStatus; // 按订单状态 (1:司机已接单，等待搬运工人; 2:搬家工人已接单; 3:进行中)

    @DateTimeFormat(pattern = "yyyy-MM-dd") // 按预约时间范围
    private LocalDate startDate; // 预约开始日期
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate endDate;   // 预约结束日期

    private String orderNumber; // 按订单号

}