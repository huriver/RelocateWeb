package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.OrdersPageQueryDTO;
import com.***REMOVED***.entity.MovingOrder;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.OrderVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

@Mapper
public interface OrderMapper {

    // 插入订单
    @Insert("INSERT INTO moving_order (customer_id, order_number, service_id, truck_type_id, order_status, " +
            "reservation_time, moving_origin, moving_destination, moving_price, mileage_cost, helper_cost, " +
            "category_price_multiplier, is_paid, number_of_helpers, notes, is_reviewed , create_time, update_time) " +
            "VALUES (#{customerId}, #{orderNumber}, #{serviceId}, #{truckTypeId}, #{orderStatus}, " +
            "#{reservationTime}, #{movingOrigin}, #{movingDestination}, #{movingPrice}, #{mileageCost}, " +
            "#{helperCost}, #{categoryPriceMultiplier}, #{isPaid}, #{numberOfHelpers}, #{notes}, #{isReviewed}, " +
            "#{createTime}, #{updateTime})")
    @Options(useGeneratedKeys = true, keyProperty = "id")
    @AutoFill(OperationType.INSERT)
    void insert(MovingOrder movingOrder);

    // 根据ID查询订单详情 (用于用户端和后台详情展示)
    OrderVO getById(Long id);

    // 根据订单号查询订单详情
    @Select("select id, customer_id, order_number, service_id, truck_type_id, driver_id, vehicle_id, order_status, " +
            "reservation_time, moving_origin, moving_destination, moving_price, mileage_cost, helper_cost, " +
            "category_price_multiplier, is_paid, payment_time, pay_method, cancel_reason, cancel_time, " +
            "moving_start_time, moving_end_time, number_of_helpers, notes, is_reviewed, create_time, update_time " +
            "from moving_order where order_number = #{orderNumber}")
    MovingOrder getByNumber(String orderNumber);

    // 更新订单
    @AutoFill(OperationType.UPDATE)
    void update(MovingOrder order);

    // 用户端历史订单分页查询
    Page<OrderVO> pageQuery(OrdersPageQueryDTO queryDTO);

    // 根据订单ID查询 MovingOrder 实体 (用于后端处理)
    @Select("select id, customer_id, order_number, service_id, truck_type_id, driver_id, vehicle_id, order_status, " +
            "reservation_time, moving_origin, moving_destination, moving_price, mileage_cost, helper_cost, " +
            "category_price_multiplier, is_paid, payment_time, pay_method, cancel_reason, cancel_time, " +
            "moving_start_time, moving_end_time, number_of_helpers, notes, is_reviewed, create_time, update_time " +
            "from moving_order where id = #{id}")
    MovingOrder getMovingOrderById(Long id);

    // 订单取消时，清除订单关联的司机和车辆信息
    @Update("UPDATE moving_order SET driver_id = NULL, vehicle_id = NULL WHERE id = #{orderId}")
    void clearOrderDriverVehicle(Long orderId);

    // 根据货车类型ID查询关联的未完成订单数量
    @Select("SELECT COUNT(*) FROM moving_order WHERE truck_type_id = #{truckTypeId} AND order_status IN (0, 1, 2, 3)")
    Integer countByTruckTypeId(Long truckTypeId);

    // 根据指派车辆ID查询关联的未完成订单数量
    // (订单状态 in (1, 2, 3) - 司机已接单、已接单、进行中)
    @Select("SELECT COUNT(*) FROM moving_order WHERE vehicle_id = #{vehicleId} " +
            "AND order_status IN (1, 2, 3)")
    Integer countByAssignedVehicleId(Long vehicleId);

    // 统计分配给特定司机且需要特定货车类型的未完成订单数量
    // 假设订单状态 1-3 表示未完成
    @Select("SELECT COUNT(*) FROM moving_order WHERE driver_id = #{driverId} AND truck_type_id = #{truckTypeId} " +
            "AND order_status IN (1, 2, 3)")
    Integer countPendingOrdersByDriverAndTruckType(Long driverId, Long truckTypeId);

    // 统计分配给特定司机的未完成订单总数 (不限货车类型)
    @Select("SELECT COUNT(*) FROM moving_order WHERE driver_id = #{driverId} AND order_status IN (1, 2, 3)")
    Integer countPendingOrdersByDriverId(Long driverId);

    // 统计引用了特定服务项的未完成订单总数
    @Select("SELECT COUNT(*) FROM moving_order WHERE service_id = #{serviceId} AND order_status IN (0, 1, 2, 3)")
    Integer countPendingOrdersByServiceId(Long serviceId);

    // 统计引用了特定服务项的总订单数量 (任何状态)
    @Select("SELECT COUNT(*) FROM moving_order WHERE service_id = #{serviceId}")
    Integer countByServiceId(Long serviceId);

    // 管理端分页查询订单列表
    Page<OrderVO> pageQueryByAdmin(OrdersPageQueryDTO ordersPageQueryDTO);


    // Future: 定时任务需要的方法 (例如根据状态和时间查询订单)
    /*
    List<MovingOrder> getByStatusAndOrderTimeLT(@Param("status") Integer status, @Param("orderTime") LocalDateTime orderTime);
     */
}