package com.***REMOVED***.controller.back;

import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.AdminOrderCancelDTO;
import com.***REMOVED***.dto.OrdersPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.OrderService;
import com.***REMOVED***.vo.OrderStatusVO;
import com.***REMOVED***.vo.OrderVO;
import com.***REMOVED***.vo.PaymentStatusVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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

    /**
     * 管理员取消订单
     *
     * @param id        要取消的订单ID
     * @param cancelDTO 包含取消原因的DTO
     * @return
     */
    @PutMapping("/cancel/{id}")
    public Result adminCancelOrder(@PathVariable Long id, @RequestBody AdminOrderCancelDTO cancelDTO) {
        log.info("管理员 {} 取消订单：订单ID={}, 原因={}", BaseContext.getCurrentId(), id, cancelDTO != null ? cancelDTO.getCancelReason() : "原因为空");
        orderService.adminCancelOrder(id, cancelDTO);
        return Result.success();
    }

//    /**
//     * 管理员手动更新订单状态 (通用纠错接口)
//     * 仅用于状态修正，不触发伴随复杂业务操作，包含状态流转校验
//     *
//     * @param status 订单要更新到的目标状态值
//     * @param id     要更新状态的订单ID
//     * @return
//     */
//    @PostMapping("/status/{status}")
//    public Result updateOrderStatus(@PathVariable Integer status, Long id) {
//        // 订单状态： 0-待接单 1-司机已接单，等待搬运工人 2-已接单 (司机和搬运工人团队已确认) 3-进行中 4-已完成 5-已取消
//        log.info("管理员 {} 手动更新订单状态：订单ID={}, 目标状态={}", BaseContext.getCurrentId(), id, status == 0 ? "待接单" : status == 1 ? "司机已接单，等待搬运工人" : status == 2 ? "已接单 (团队确认)" : status == 3 ? "进行中" : status == 4 ? "已完成" : "已取消");
//        orderService.updateStatus(id, status);
//        return Result.success();
//    }


}