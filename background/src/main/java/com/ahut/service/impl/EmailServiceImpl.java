package com.ahut.service.impl;


import com.ahut.constant.OrderStatusConstant;
import com.ahut.entity.Customer;
import com.ahut.entity.Driver;
import com.ahut.entity.MovingOrder;
import com.ahut.mapper.CustomerMapper;
import com.ahut.mapper.DriverMapper;
import com.ahut.service.EmailService;
import com.ahut.vo.MoverVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 邮件服务实现类
 */
@Service
@Slf4j
public class EmailServiceImpl implements EmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Autowired
    private DriverMapper driverMapper;

    @Autowired
    private CustomerMapper customerMapper;

    @Value("${spring.mail.username}")
    private String fromEmail;

    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");


    /**
     * 异步发送订单状态变更通知邮件给消费者 (统一方法)
     *
     * @param updatedOrder   包含最新信息的订单对象
     * @param newStatus      新的订单状态
     * @param assignedMovers 已指派的搬运工列表 (仅在状态 2 需要传递，其他状态传空列表)
     */
    @Async // 异步执行
    @Override
    public void sendOrderStatusEmailToCustomer(MovingOrder updatedOrder, Integer newStatus, List<MoverVO> assignedMovers) {
        try {
            Customer customer = customerMapper.getById(updatedOrder.getCustomerId());
            if (customer == null || customer.getEmail() == null || customer.getEmail().isEmpty()) {
                log.warn("发送订单状态邮件失败：订单ID {} 对应的客户不存在或邮箱为空。", updatedOrder.getId());
                return;
            }

            // 2. 生成邮件内容 - 调用 generateEmailBody，并传递所有必要数据
            String toEmail = customer.getEmail();
            String subject = "您的搬家订单状态已更新！";
            // 将所有必要数据都传递给 generateEmailBody
            String body = generateEmailBody(updatedOrder, newStatus, assignedMovers);

            // 3. 构建并发送邮件
            // ... (构建 SimpleMailMessage 并发送邮件的逻辑，保持不变) ...
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom(fromEmail);
            message.setTo(toEmail);
            message.setSubject(subject);
            message.setText(body);
            mailSender.send(message);

            log.info("订单状态变更邮件已成功发送给客户 {} (邮箱: {})，订单ID: {}，新状态: {}",
                    customer.getName(), toEmail, updatedOrder.getId(), newStatus);
        } catch (MailException e) {
            log.error("发送订单状态变更邮件失败（邮件发送异常），订单ID: {}，错误信息: {}",
                    updatedOrder != null ? updatedOrder.getId() : "null", e.getMessage(), e);
        } catch (Exception e) {
            log.error("发送订单状态变更邮件遇到未知错误，订单ID: {}，错误信息: {}",
                    updatedOrder != null ? updatedOrder.getId() : "null", e.getMessage(), e);
        }
    }

    /**
     * 根据订单信息、新状态和搬运工列表生成邮件正文内容
     * 在方法内部查询司机信息
     *
     * @param order          订单实体
     * @param newStatus      新的订单状态
     * @param assignedMovers 已指派的搬运工列表 (从调用方传入)
     * @return 邮件正文
     */
    private String generateEmailBody(MovingOrder order, Integer newStatus, List<MoverVO> assignedMovers) {
        String statusDescription = OrderStatusConstant.getDescription(newStatus);

        StringBuilder bodyBuilder = new StringBuilder();
        bodyBuilder.append("尊敬的客户：\n\n");
        bodyBuilder.append("您的搬家订单 [").append(order.getOrderNumber()).append("] 状态已更新。\n");
        bodyBuilder.append("当前最新状态为：").append(statusDescription).append("\n\n");

        // 根据状态添加更多详细信息
        if (newStatus.equals(OrderStatusConstant.PENDING_ACCEPTANCE)) {
            // 状态 0 (待接单) 的邮件内容
            bodyBuilder.append("您的订单已成功提交，正在等待司机和搬运工人接单。\n");
            bodyBuilder.append("订单已发布到平台，请在您的订单详情页关注接单进展。\n");
            // 添加支付提示
            bodyBuilder.append("请注意：为了确保订单能被及时处理，如果您的订单尚未支付，请尽快在15分钟内完成支付。如已完成支付，请忽略此提示。\n");
            bodyBuilder.append("请保持电话畅通，以便接单的司机和搬运工人联系。\n");

        } else if (newStatus.equals(OrderStatusConstant.DRIVER_ACCEPTED_WAITING_MOVERS)) {
            // 状态 1 (司机已接单，等待搬运工人) 的邮件内容
            bodyBuilder.append("您的订单已被司机接单。\n");
            // *** 在这里查询并添加司机的姓名和电话 ***
            if (order.getDriverId() != null) {
                Driver driver = driverMapper.getById(order.getDriverId()); // *** 查询司机信息 ***
                if (driver != null) {
                    bodyBuilder.append("接单司机：").append(driver.getName()).append("\n");
                    bodyBuilder.append("司机电话：").append(driver.getPhone()).append("\n");
                } else {
                    bodyBuilder.append("接单司机信息暂不可用。\n");
                }
            }
            bodyBuilder.append("搬家团队正在组建中，请关注搬运工人就位后的状态更新。\n");

        } else if (newStatus.equals(OrderStatusConstant.ACCEPTED)) {
            // 状态 2 (已接单 团队确认) 的邮件内容
            bodyBuilder.append("您的搬家团队已组建完成！\n");
            // *** 在这里查询并添加司机的姓名和电话 ***
            if (order.getDriverId() != null) {
                Driver driver = driverMapper.getById(order.getDriverId()); // *** 查询司机信息 ***
                if (driver != null) {
                    bodyBuilder.append("司机：").append(driver.getName()).append(" (电话: ").append(driver.getPhone()).append(")\n");
                } else {
                    bodyBuilder.append("司机：信息暂不可用。\n");
                }
            }

            // *** 使用传入的 assignedMovers 列表 ***
            if (assignedMovers != null && !assignedMovers.isEmpty()) {
                bodyBuilder.append("搬运工人：\n");
                for (MoverVO moverVO : assignedMovers) { // 遍历传入的列表
                    bodyBuilder.append("- ").append(moverVO.getName()).append(" (电话: ").append(moverVO.getPhone()).append(")\n");
                }
            } else {
                bodyBuilder.append("搬运工人：暂无信息。\n");
            }

            bodyBuilder.append("司机和搬运工人已准备好，即将按照您预约的时间为您服务。\n");
            // 添加预约时间 order.getReservationTime() 的详细信息
            if (order.getReservationTime() != null) {
                bodyBuilder.append("预约时间：").append(DATE_TIME_FORMATTER.format(order.getReservationTime())).append("\n");
            }

        } else if (newStatus.equals(OrderStatusConstant.IN_PROGRESS)) {
            // 状态 3 (进行中) 的邮件内容
            bodyBuilder.append("您的搬家服务已正式开始！\n");
            // 可以选择再次添加司机和搬运工信息，需要在这里查询司机并使用传入的 mover 列表
            if (order.getMovingStartTime() != null) {
                bodyBuilder.append("服务开始时间：").append(DATE_TIME_FORMATTER.format(order.getMovingStartTime())).append("\n");
            }
            bodyBuilder.append("请您保持沟通畅通，以便服务顺利进行。\n");

        } else if (newStatus.equals(OrderStatusConstant.COMPLETED)) {
            // 状态 4 (已完成) 的邮件内容
            bodyBuilder.append("您的搬家服务已圆满完成！\n");
            if (order.getMovingEndTime() != null) {
                bodyBuilder.append("服务结束时间：").append(DATE_TIME_FORMATTER.format(order.getMovingEndTime())).append("\n");
            }
            bodyBuilder.append("请及时对本次服务进行评价。\n");

        } else if (newStatus.equals(OrderStatusConstant.CANCELLED)) {
            // 状态 5 (已取消) 的邮件内容
            bodyBuilder.append("您的订单已取消。\n");
            if (order.getCancelReason() != null && !order.getCancelReason().isEmpty()) {
                bodyBuilder.append("取消原因：").append(order.getCancelReason()).append("\n");
            }
        }

        bodyBuilder.append("\n感谢您使用我们的服务！");
        return bodyBuilder.toString();
    }

}