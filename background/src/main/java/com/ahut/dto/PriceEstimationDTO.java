package com.***REMOVED***.dto;

import lombok.Data;

/**
 * 价格估算请求参数DTO
 */
@Data
public class PriceEstimationDTO {
    private Long serviceId;
    private String originAddress;
    private String destinationAddress;
    private Integer numberOfHelpers;
}