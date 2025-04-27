package com.***REMOVED***.vo;

import lombok.Data;

import java.util.List;

@Data
public class DriverTruckTypeRelationVO {
    private Long driverId; // 司机ID
    private String driverName; // 司机姓名

    // 嵌套列表，每个元素代表一个资质关联的详细信息
    private List<TruckTypeSimpleVO> truckTypeSimpleVOList;
}
