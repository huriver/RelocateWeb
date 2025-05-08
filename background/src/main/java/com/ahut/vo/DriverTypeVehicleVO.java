package com.***REMOVED***.vo;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class DriverTypeVehicleVO {

    // 货车类型信息 (从 truck_type 表获取)
    private Long truckTypeId;           // 货车类型ID
    private String truckTypeName;       // 货车类型名称 (例如: 小型面包车)
    private String truckTypeCapacity;   // 车厢容量 (例如: 1.8*1.3*1.1m)
    private String truckTypeDescription; // 货车类型描述
    private BigDecimal truckTypeBaseFare; // 起步价

    // 对应这个货车类型，分配给司机的具体车辆信息
    private AssignedVehicleVO assignedVehicle; // 嵌套的车辆 VO

}