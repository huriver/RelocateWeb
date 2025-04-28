package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.OrdersPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.OrderService;
import com.***REMOVED***.vo.OrderStatusVO;
import com.***REMOVED***.vo.OrderVO;
import com.***REMOVED***.vo.PaymentStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController("backOrderController")
@RequestMapping("/back/order")
@Slf4j
public class OrderController {
    @Autowired
    private OrderService orderService;


    /**
     * 获取所有订单状态列表接口 (用于前端管理端订单筛选下拉框/Tab)
     *
     * @return 订单状态VO列表
     */
    @GetMapping("/status")
    public Result<List<OrderStatusVO>> getStatus() {
        log.info("后台端获取所有订单状态列表");
        List<OrderStatusVO> statusList = orderService.getOrderStatusList();
        return Result.success(statusList);
    }

    /**
     * 获取所有支付状态列表接口 (用于前端管理端订单筛选下拉框)
     *
     * @return 支付状态VO列表
     */
    @GetMapping("/paymentStatus")
    public Result<List<PaymentStatusVO>> getPaymentStatuses() {
        log.info("后台端获取所有支付状态列表");
        List<PaymentStatusVO> statusList = orderService.getPaymentStatusList();
        return Result.success(statusList);
    }

    /**
     * 后台端分页查询订单
     *
     * @param ordersPageQueryDTO 查询条件 (包含管理端筛选字段)
     * @return 分页结果 (PageResult<OrderVO>)
     */
    @GetMapping("/page")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("后台端订单分页查询：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQueryByAdmin(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 后台端查询订单详情
     *
     * @param id 订单ID
     * @return 订单详情VO
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getById(@PathVariable Long id) {
        log.info("后台端查询订单详情，订单ID：{}", id);
        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

}