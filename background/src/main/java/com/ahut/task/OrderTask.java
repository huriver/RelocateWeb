package com.ahut.task;


import com.ahut.entity.MovingOrder;
import com.ahut.mapper.OrderMapper;
import com.ahut.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Component
@Slf4j
public class OrderTask {

    @Autowired
    private OrderMapper movingOrderMapper;

    @Autowired
    private OrderService movingOrderService;

    /**
     * 处理支付超时的订单
     * 每分钟执行一次，从订单创建时间算起15分钟未支付的待接单订单将被取消
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟触发一次
    public void autoCancelTimeoutOrders() {
        log.info("定时任务开始执行：检查支付超时订单 " + LocalDateTime.now());
        // 计算15分钟前的时间点
        LocalDateTime timeoutThreshold = LocalDateTime.now().minusMinutes(15);
        // 查询符合条件的订单 (状态为待接单(0)，未支付(0)，创建时间 < 15分钟前)
        List<MovingOrder> timeoutOrders = movingOrderMapper.getTimeoutUnpaidOrders(timeoutThreshold);

        if (timeoutOrders != null && !timeoutOrders.isEmpty()) {
            log.info("发现 " + timeoutOrders.size() + " 个支付超时订单，准备自动取消。");
            // 遍历并取消订单
            for (MovingOrder order : timeoutOrders) {
                movingOrderService.processPaymentTimeoutCancellation(order.getId(), "支付超时，自动取消");
                log.info("订单 " + order.getId() + " 已自动取消。");
            }
        } else {
            log.info("没有发现支付超时订单。");
        }
    }

}
