package com.***REMOVED***.service;

import com.***REMOVED***.dto.OrderSubmitDTO;
import com.***REMOVED***.dto.PriceEstimationDTO;
import com.***REMOVED***.vo.OrderSubmitVO;
import com.***REMOVED***.vo.PriceEstimationResultVO;

// 搬家订单相关的 Service 接口
public interface OrderService {

    // 估算搬家订单价格
    PriceEstimationResultVO estimatePrice(PriceEstimationDTO estimationDTO);

    // 用户提交订单
    OrderSubmitVO submitOrder(OrderSubmitDTO submitDTO);

}