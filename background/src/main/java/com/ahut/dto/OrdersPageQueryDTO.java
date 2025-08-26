package com.ahut.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

/**
 * 用户/管理端历史订单分页查询参数DTO
 * 同时用于用户端和管理端，管理端使用更多字段进行筛选
 */
@Data
public class OrdersPageQueryDTO {
    private Integer page; // 页码
    private Integer pageSize; // 每页记录数

    // 用户端使用此字段过滤自己的订单 (由后端设置，非前端传递)
    // 管理端查询所有用户的订单，此字段在管理端Service实现中不会被设置或使用
    private Long userId;

    // 以下为管理端常用的筛选条件 (也可以用于用户端，如果用户端需要复杂筛选的话)
    private String orderNumber; // 订单号
    private Integer orderStatus; // 订单状态
    private Integer isPaid; // 支付状态

    // 关键词搜索条件
    private String customerKeyword; // 客户姓名或电话关键词 (后端模糊匹配姓名和电话)
    private String driverName; // 司机姓名 (后端模糊匹配司机姓名)
    private String serviceItemName; // 服务项名称 (后端模糊匹配服务项名称)

    // 关联ID筛选 (精确匹配)
    private Long categoryId; // 服务类型ID
    private Long truckTypeId; // 货车类型ID

    // 时间范围筛选
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    // 指定前端传递的格式
    private LocalDateTime createTimeStart; // 下单时间开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    // 指定前端传递的格式
    private LocalDateTime createTimeEnd; // 下单时间结束
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    // 指定前端传递的格式
    private LocalDateTime reservationTimeStart; // 预约时间开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")    // 指定前端传递的格式
    private LocalDateTime reservationTimeEnd; // 预约时间结束

}