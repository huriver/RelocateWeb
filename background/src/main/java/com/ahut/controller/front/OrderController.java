package com.***REMOVED***.controller.front;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.MovingOrder;
import com.***REMOVED***.exception.BusinessException;
import com.***REMOVED***.mapper.OrderMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.OrderService;
import com.***REMOVED***.vo.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController("frontOrderController")
@RequestMapping("/front/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderMapper orderMapper;


    /**
     * 订单价格估算接口
     *
     * @param estimationDTO
     * @return
     */
    @PostMapping("/estimate")
    public Result<PriceEstimationResultVO> estimatePrice(@RequestBody PriceEstimationDTO estimationDTO) {
        log.info("用户端估算搬家订单价格接口调用，参数：{}", estimationDTO);
        PriceEstimationResultVO priceEstimationResultVO = orderService.estimatePrice(estimationDTO);
        return Result.success(priceEstimationResultVO);
    }

    /**
     * 用户提交订单
     *
     * @param submitDTO
     * @return
     */
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submit(@RequestBody OrderSubmitDTO submitDTO) {
        log.info("用户提交订单，参数：{}", submitDTO);
        OrderSubmitVO submitResultVO = orderService.submitOrder(submitDTO);
        return Result.success(submitResultVO);
    }

    /**
     * 用户发起订单支付 (模拟)
     *
     * @param ordersPaymentDTO
     * @return
     */
    @PutMapping("/payment")
    public Result<OrderPaymentVO> payment(@RequestBody OrdersPaymentDTO ordersPaymentDTO) {
        log.info("用户发起订单支付接口调用，参数：{}", ordersPaymentDTO);
        OrderPaymentVO orderPaymentVO = orderService.payment(ordersPaymentDTO);
        return Result.success(orderPaymentVO);
    }

    /**
     * 用户端历史订单分页查询
     *
     * @param ordersPageQueryDTO
     * @return
     */
    @GetMapping("/historyOrders")
    public Result<PageResult> page(OrdersPageQueryDTO ordersPageQueryDTO) {
        log.info("用户端历史订单查询，参数：{}", ordersPageQueryDTO);
        PageResult pageResult = orderService.pageQuery(ordersPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 用户端查询订单详情
     *
     * @param id
     * @return
     */
    @GetMapping("/orderDetail/{id}")
    public Result<OrderVO> getOrderDetail(@PathVariable Long id) {
        log.info("用户端查询订单详情，订单ID：{}", id);

        MovingOrder order = orderMapper.getMovingOrderById(id);
        // 校验订单是否存在
        if (order == null) {
            log.error("用户端查询订单详情失败，订单不存在：ID {}", id);
            throw new BusinessException(MessageConstant.ORDER_NOT_FOUND);
        }
        // 校验订单是否属于当前用户
        if (!order.getCustomerId().equals(BaseContext.getCurrentId())) {
            log.error("用户端查询订单详情失败，订单 {} 属于用户 {}，不属于当前用户 {}",
                    id, order.getCustomerId(), BaseContext.getCurrentId());
            throw new BusinessException(MessageConstant.ORDER_NOT_BELONG_TO_CURRENT_USER);
        }

        OrderVO orderVO = orderService.getOrderDetail(id);
        return Result.success(orderVO);
    }

    /**
     * 用户端取消订单
     *
     * @param id
     * @param cancelDTO
     * @return
     */
    @PutMapping("/cancel/{id}")
    public Result<String> cancel(@PathVariable Long id, @RequestBody OrderCancelDTO cancelDTO) {
        log.info("用户端取消订单，订单ID：{}，参数：{}", id, cancelDTO);
        orderService.cancelOrder(id, cancelDTO);
        return Result.success();
    }

    /**
     * 获取所有订单状态列表接口 (用于前端管理端订单筛选下拉框/Tab)
     *
     * @return 订单状态VO列表
     */
    @GetMapping("/status")
    public Result<List<OrderStatusVO>> getStatus() {
        log.info("用户端获取所有订单状态列表");
        List<OrderStatusVO> statusList = orderService.getOrderStatusList();
        return Result.success(statusList);
    }

}