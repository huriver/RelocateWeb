package com.***REMOVED***.service;

import com.***REMOVED***.dto.*;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.*;

import java.util.List;


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

    // 用户端/管理端根据订单id查询订单详情
    OrderVO getOrderDetail(Long id);

    // 用户取消订单
    void cancelOrder(Long id, OrderCancelDTO orderCancelDTO);

    // 获取所有订单状态列表
    List<OrderStatusVO> getOrderStatusList();

    // 获取所有支付状态列表
    List<PaymentStatusVO> getPaymentStatusList();

    // 管理端分页查询订单列表
    PageResult pageQueryByAdmin(OrdersPageQueryDTO ordersPageQueryDTO);

    // 管理员取消订单
    void adminCancelOrder(Long id, AdminOrderCancelDTO cancelDTO);

    // 管理员强制完成订单
    void forceComplete(Long id);

}