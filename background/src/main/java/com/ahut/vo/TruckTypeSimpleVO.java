package com.***REMOVED***.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TruckTypeSimpleVO {
    private Long truckTypeId; // 货车类型ID
    private String truckTypeName; // 货车类型名称
}
