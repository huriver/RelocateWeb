package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 搬家工人端 - 历史订单分页查询条件 DTO
 */
@Data
public class MoverHistoricalOrderPageQueryDTO {

    private Integer page;       // 页码
    private Integer pageSize;   // 每页记录数

    // --- 可选筛选条件 ---
    private String orderNumber; // 按订单号 (支持模糊查询)
    private String customerName; // 按客户姓名 (支持模糊查询)

    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 按订单实际结束时间范围
    private LocalDateTime beginEndTime; // 订单实际结束时间开始范围
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endEndTime;   // 订单实际结束时间结束范围

    // 按订单状态过滤 (主要针对历史订单的特定状态)
    private Integer orderStatus; // 订单状态：4-已完成，5-已取消

}