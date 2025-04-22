package com.***REMOVED***.service;

import com.***REMOVED***.dto.OrdersPageQueryDTO;
import com.***REMOVED***.dto.OrderSubmitDTO;
import com.***REMOVED***.dto.OrdersPaymentDTO;
import com.***REMOVED***.dto.PriceEstimationDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.OrderPaymentVO;
import com.***REMOVED***.vo.OrderSubmitVO;
import com.***REMOVED***.vo.OrderVO;
import com.***REMOVED***.vo.PriceEstimationResultVO;


public interface OrderService {

    // 估算搬家订单价格
    PriceEstimationResultVO estimatePrice(PriceEstimationDTO estimationDTO);

    // 用户提交订单
    OrderSubmitVO submitOrder(OrderSubmitDTO submitDTO);

    // 支付订单
    OrderPaymentVO payment(OrdersPaymentDTO ordersPaymentDTO);

    // 用于支付回调处理 (模拟)
    void paySuccess(String orderNumber, Integer payMethod);

    // 用户端历史订单分页查询
    PageResult pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    // 用户端根据订单id查询订单详情
    OrderVO getOrderDetail(Long id);

}