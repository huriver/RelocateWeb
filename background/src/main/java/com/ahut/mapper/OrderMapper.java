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

    // 根据订单号查询订单详情
    @Select("select id, customer_id, order_number, service_id, truck_type_id, driver_id, vehicle_id, " +
            "order_status, reservation_time, moving_origin, moving_destination, moving_price, is_paid, " +
            "payment_time, pay_method, cancel_reason, moving_start_time, moving_end_time, number_of_helpers, " +
            "notes, create_time, update_time " +
            "from moving_order where order_number = #{orderNumber}")
    MovingOrder getByNumber(String orderNumber);


    // 更新订单
    void update(MovingOrder order);


    // Future: 分页查询订单列表的方法 (需要根据 OrdersPageQueryDTO 进行过滤和分页)
    /*
    Page<MovingOrder> pageQuery(OrdersPageQueryDTO queryDTO);
     */


    // Future: 定时任务需要的方法 (例如根据状态和时间查询订单)
    /*
    List<MovingOrder> getByStatusAndOrderTimeLT(@Param("status") Integer status, @Param("orderTime") LocalDateTime orderTime);
     */
}