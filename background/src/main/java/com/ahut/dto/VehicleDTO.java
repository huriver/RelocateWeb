package com.ahut.dto;

import lombok.Data;

@Data
public class VehicleDTO {
    private Long id;

    private Long driverId; // 司机ID

    private Long truckTypeId; // 货车类型ID

    private String licensePlateNumber; // 车牌号，唯一

    private String vehicleBrand; // 车辆品牌 (可选)
}