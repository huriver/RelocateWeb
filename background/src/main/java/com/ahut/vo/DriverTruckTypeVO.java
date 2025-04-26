package com.***REMOVED***.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class DriverTruckTypeVO {
    private Long driverId; // 司机ID
    private String driverName; // 司机姓名

    private Long truckTypeId; // 货车类型ID
    private String truckTypeName; // 货车类型名称

    private LocalDateTime createTime; // 关联创建时间
    private LocalDateTime updateTime; // 关联更新时间
}