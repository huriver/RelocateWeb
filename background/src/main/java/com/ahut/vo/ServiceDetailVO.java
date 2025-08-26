package com.ahut.vo;

import com.ahut.entity.Service;
import com.ahut.entity.TruckType;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigDecimal;

/**
 * 用户端查询服务项详情VO
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承 Service 后，需要包含父类字段来生成 equals 和 hashCode
public class ServiceDetailVO extends Service {
    private String categoryName;
    private BigDecimal categoryPriceMultiplier;
    private TruckType truckType;
    private BigDecimal perHelperCost;
}