package com.ahut.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Vehicle {
    // 主键，自增
    private Long id;

    // 司机ID，外键，关联 driver 表的 id
    private Long driverId;

    // 货车类型ID，外键，关联 truck_type 表的 id
    private Long truckTypeId;

    // 车牌号，唯一
    private String licensePlateNumber;

    // 车辆品牌 (可选)
    private String vehicleBrand;

    // 创建时间
    private LocalDateTime createTime;

    // 修改时间
    private LocalDateTime updateTime;

    // 创建用户ID
    private Long createUser;

    // 更新用户ID
    private Long updateUser;

}