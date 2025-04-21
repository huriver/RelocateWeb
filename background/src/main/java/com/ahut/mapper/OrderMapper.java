package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.entity.MovingOrder;
import com.***REMOVED***.enumeration.OperationType;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {

    // 插入订单
    @Insert("INSERT INTO moving_order (customer_id, order_number, service_id, truck_type_id, order_status, " +
            "reservation_time, moving_origin, moving_destination, moving_price, is_paid, number_of_helpers, " +
            "notes, create_time, update_time) " +
            "VALUES (#{customerId}, #{orderNumber}, #{serviceId}, #{truckTypeId}, #{orderStatus}, " +
            "#{reservationTime}, #{movingOrigin}, #{movingDestination}, #{movingPrice}, #{isPaid}, " +
            "#{numberOfHelpers}, #{notes}, #{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(OperationType.INSERT)
    void insert(MovingOrder movingOrder);

//    /**
//     * 根据ID查询订单详情
//     *
//     * @param id 订单ID
//     * @return MovingOrder 实体
//     */
//    @Select("select * from moving_order where id = #{id}")
//    MovingOrder getById(Long id);

//    /**
//     * 根据订单号查询订单详情
//     * 用于支付回调等场景
//     *
//     * @param orderNumber 订单号
//     * @return MovingOrder 实体
//     */
//    @Select("select * from moving_order where order_number = #{orderNumber}")
//    MovingOrder getByNumber(String orderNumber);


    /**
     * 更新订单信息 (通用更新方法)
     *
     * @param order 包含要更新字段的 MovingOrder 实体对象 (非null字段会被更新)
     */
    // 示例：使用动态SQL更新部分字段
//    @Update("<script>" +
//            "update moving_order " +
//            "<set>" +
//            " <if test='orderStatus != null'> order_status = #{orderStatus}, </if>" +
//            " <if test='isPaid != null'> is_paid = #{isPaid}, </if>" +
//            " <if test='paymentTime != null'> payment_time = #{paymentTime}, </if>" +
//            " <if test='payMethod != null'> pay_method = #{payMethod}, </if>" +
//            " <if test='cancelReason != null'> cancel_reason = #{cancelReason}, </if>" +
//            " <if test='movingStartTime != null'> moving_start_time = #{movingStartTime}, </if>" +
//            " <if test='movingEndTime != null'> moving_end_time = #{movingEndTime}, </if>" +
//            " <if test='driverId != null'> driver_id = #{driverId}, </if>" +
//            " <if test='vehicleId != null'> vehicle_id = #{vehicleId}, </if>" +
//            " update_time = now() " + // 总是更新 update_time
//            "</set>" +
//            "where id = #{id}" +
//            "</script>")
//    void update(MovingOrder order);


    // Future: 分页查询订单列表的方法 (需要根据 OrdersPageQueryDTO 进行过滤和分页)
    /*
    Page<MovingOrder> pageQuery(OrdersPageQueryDTO queryDTO);
     */


    // Future: 定时任务需要的方法 (例如根据状态和时间查询订单)
    /*
    List<MovingOrder> getByStatusAndOrderTimeLT(@Param("status") Integer status, @Param("orderTime") LocalDateTime orderTime);
     */
}