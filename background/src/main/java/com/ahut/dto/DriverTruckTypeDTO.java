package com.***REMOVED***.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DriverTruckTypeDTO {
    private Long driverId; // 司机的ID
    private Long truckTypeId; // 货车类型的ID
}
