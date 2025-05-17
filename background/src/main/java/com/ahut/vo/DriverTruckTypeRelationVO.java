package com.***REMOVED***.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DriverTruckTypeRelationVO {
    private Long driverId; // 司机ID
    private String driverName; // 司机姓名

    // 可驾驶的货车类型简要信息列表
    private List<TruckTypeSimpleVO> truckTypeSimpleVOList;
}
