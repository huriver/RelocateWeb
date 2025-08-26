package com.ahut.vo;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class VehicleVO {
    private Long id;
    private Long driverId; // 司机ID
    private Long truckTypeId; // 货车类型ID
    private String licensePlateNumber; // 车牌号
    private String vehicleBrand; // 车辆品牌
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
    private Long createUser; // 创建用户ID
    private Long updateUser; // 更新用户ID

    // ====== 关联表的名称字段 (通过 JOIN 获取) ======
    private String driverName; // 所属司机姓名
    private String truckTypeName; // 货车类型名称
    private String createUserName; // 创建管理员姓名
    private String updateUserName; // 更新管理员姓名
}