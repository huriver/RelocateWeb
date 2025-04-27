package com.***REMOVED***.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverTruckType {
    // 司机ID，外键，关联 driver 表的 id (联合主键的一部分)
    private Long driverId;

    // 货车类型ID，外键，关联 truck_type 表的 id (联合主键的一部分)
    private Long truckTypeId;

    // 创建时间
    private LocalDateTime createTime;

    // 修改时间
    private LocalDateTime updateTime;
}