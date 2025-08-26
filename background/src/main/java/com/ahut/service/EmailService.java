package com.ahut.service;

import com.ahut.entity.MovingOrder;
import com.ahut.vo.MoverVO;

import java.util.List;

public interface EmailService {

    // 异步发送订单状态变更通知邮件给消费者
    void sendOrderStatusEmailToCustomer(MovingOrder updatedOrder, Integer newStatus, List<MoverVO> assignedMovers);

}