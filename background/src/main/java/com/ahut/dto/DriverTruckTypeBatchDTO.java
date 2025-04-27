package com.***REMOVED***.dto;

import lombok.Data;

import java.util.List;

@Data
public class DriverTruckTypeBatchDTO {

    private Long driverId; // 司机的ID

    private List<Long> truckTypeIds; // 被选中的货车类型ID列表
}