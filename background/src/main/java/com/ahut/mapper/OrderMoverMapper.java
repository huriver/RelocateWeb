package com.***REMOVED***.mapper;

import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface OrderMoverMapper {

    // 根据订单ID删除 order_mover 表中的关联记录
    @Delete("delete from order_mover where order_id = #{orderId}")
    void deleteByOrderId(Long orderId);

    // 根据订单ID查询关联的搬运工人ID列表
    @Select("select mover_id from order_mover where order_id = #{orderId}")
    List<Long> getMoverIdsByOrderId(Long orderId);

    // Future: 可以添加其他操作 order_mover 表的方法，例如：
    // @Insert("insert into order_mover (order_id, mover_id) values (#{orderId}, #{moverId})")
    // void insert(@Param("orderId") Long orderId, @Param("moverId") Long moverId);
    // List<Long> getMoverIdsByOrderId(@Param("orderId") Long orderId);
}