package com.ahut.dto;

import lombok.Data;

@Data
public class DriverTruckTypePageQueryDTO {
    private int page; // 页码
    private int pageSize; // 每页记录数

    // 查询条件
    private String driverName; // 司机姓名 (用于模糊查询文本框)
    private Long truckTypeId; // 货车类型ID (用于下拉列表选择)
}