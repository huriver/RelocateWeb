package com.***REMOVED***.service;

import com.***REMOVED***.dto.PriceEstimationDTO;
import com.***REMOVED***.vo.PriceEstimationResultVO;

// 搬家订单相关的 Service 接口
public interface OrderService {

    // 估算搬家订单价格
    PriceEstimationResultVO estimatePrice(PriceEstimationDTO estimationDTO);

    // Future: 订单提交接口方法签名
    // OrderSubmitVO submitOrder(OrderSubmitDTO submitDTO);

}