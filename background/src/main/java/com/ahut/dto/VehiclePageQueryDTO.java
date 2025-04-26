package com.***REMOVED***.dto;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;

@Data
public class VehiclePageQueryDTO {
    private int page; // 页码
    private int pageSize; // 每页记录数
    private String licensePlateNumber; // 车牌号搜索
    private String driverName; // 按所属司机姓名模糊查询
    private Long truckTypeId; // 按货车类型ID筛选
    private String vehicleBrand; // 车辆品牌搜索
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeStart; // 创建时间范围开始
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTimeEnd; // 创建时间范围结束
}