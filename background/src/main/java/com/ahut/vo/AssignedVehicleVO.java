package com.ahut.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AssignedVehicleVO {

    private Long vehicleId;             // 车辆ID
    private String licensePlateNumber;  // 车牌号 (例如: 京A·ahut5)
    private String vehicleBrand;        // 车辆品牌 (例如: 福特)

}