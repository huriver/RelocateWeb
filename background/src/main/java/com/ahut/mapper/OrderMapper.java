package com.***REMOVED***.mapper;

import com.***REMOVED***.annotation.AutoFill;
import com.***REMOVED***.dto.DriverAvailableOrderPageQueryDTO;
import com.***REMOVED***.dto.OrdersPageQueryDTO;
import com.***REMOVED***.entity.MovingOrder;
import com.***REMOVED***.enumeration.OperationType;
import com.***REMOVED***.vo.DriverAvailableOrderDetailVO;
import com.***REMOVED***.vo.DriverAvailableOrderSummaryVO;
import com.***REMOVED***.vo.OrderVO;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.*;

import java.time.LocalDateTime;
import java.util.List;

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

    // 统计指定消费者未完成订单数量   订单状态 0, 1, 2, 3 通常表示未完成/进行中
    @Select("SELECT COUNT(*) FROM moving_order WHERE customer_id = #{customerId} AND order_status IN (0, 1, 2, 3)")
    Integer countPendingOrdersByCustomerId(Long customerId);

    // 统计关联到指定搬运工的未完成订单数量 (例如: 状态为 2, 3)
    @Select("SELECT count(DISTINCT mo.id) FROM moving_order mo JOIN order_mover om ON mo.id = om.order_id " +
            "WHERE om.mover_id = #{moverId} AND mo.order_status IN (2, 3)")
    int countPendingOrdersByMoverId(Long moverId);

    // 查询支付超时的未支付订单     条件：状态为待接单(0)，未支付(0)，创建时间早于指定阈值
    @Select("SELECT id, customer_id, order_number, service_id, truck_type_id, driver_id, vehicle_id, " +
            "order_status, reservation_time, moving_origin, moving_destination, moving_price, mileage_cost, " +
            "helper_cost, category_price_multiplier, is_paid, payment_time, pay_method, cancel_reason, " +
            "cancel_time, moving_start_time, moving_end_time, number_of_helpers, notes, is_reviewed, " +
            "create_time, update_time " +
            "FROM moving_order WHERE order_status = 0 AND is_paid = 0 AND create_time < #{timeoutThreshold}")
    List<MovingOrder> getTimeoutUnpaidOrders(LocalDateTime timeoutThreshold);

    // 分页查询待接订单列表 (已根据司机能力过滤，并应用可选筛选)
    Page<DriverAvailableOrderSummaryVO> driverPageQueryAvailable(
            @Param("dto") DriverAvailableOrderPageQueryDTO pageQueryDTO, // 使用 @Param 给 DTO 起别名
            @Param("currentDriverId") Long currentDriverId); // 使用 @Param 给司机ID起别名

    // 司机端根据订单ID和司机ID查询待接订单详情
    DriverAvailableOrderDetailVO driverGetAvailableDetail(Long orderId, Long currentDriverId);

}