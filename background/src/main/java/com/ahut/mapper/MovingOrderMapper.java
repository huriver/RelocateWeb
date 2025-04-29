package com.***REMOVED***.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface MovingOrderMapper {

    // 统计关联到指定搬运工的未完成订单数量 (例如: 状态为 2, 3)
    @Select("SELECT count(DISTINCT mo.id) FROM moving_order mo JOIN order_mover om ON mo.id = om.order_id " +
            "WHERE om.mover_id = #{moverId} AND mo.order_status IN (2, 3)")
    int countPendingOrdersByMoverId(Long moverId);

}