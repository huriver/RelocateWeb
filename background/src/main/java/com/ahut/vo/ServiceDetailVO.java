package com.***REMOVED***.vo;

import com.***REMOVED***.entity.Service;
import com.***REMOVED***.entity.TruckType;
import lombok.Data;
import lombok.EqualsAndHashCode;


@Data
@EqualsAndHashCode(callSuper = true) // 继承 Service 后，需要包含父类字段来生成 equals 和 hashCode
public class ServiceDetailVO extends Service {
    private String categoryName;
    private TruckType truckType;
}