package com.***REMOVED***.vo;

import lombok.Data;

@Data
public class AssignedVehicleVO {

    private Long vehicleId;             // 车辆ID
    private String licensePlateNumber;  // 车牌号 (例如: 京A·***REMOVED***5)
    private String vehicleBrand;        // 车辆品牌 (例如: 福特)

}