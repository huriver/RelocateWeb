package com.***REMOVED***.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

// 司机历史订单分页查询参数DTO
@Data
public class DriverHistoricalOrderPageQueryDTO {

    private Integer page; // 当前页码
    private Integer pageSize; // 每页记录数

    // --- 查询条件 ---
    private String orderNumber; // 订单号 (模糊查询)
    private String customerName; // 客户姓名 (模糊查询)

    // 按时间范围过滤 (这里选择按实际结束时间过滤，更符合历史订单概念)
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") // 方便前端传入字符串时间自动转换
    private LocalDateTime beginEndTime; // 订单实际结束时间开始范围
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime endEndTime; // 订单实际结束时间结束范围

    // 按订单状态过滤 (主要过滤“已完成”和“已取消”中的子集)
    private Integer orderStatus; // 订单状态：4-已完成，5-已取消

}