package com.***REMOVED***.mapper;

import com.***REMOVED***.vo.MoverVO;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
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

    // 根据订单ID查询分配的搬家工人列表
    @Select("SELECT m.id, m.name, m.phone FROM mover m JOIN order_mover om ON m.id = om.mover_id " +
            "WHERE om.order_id = #{orderId}")
    List<MoverVO> getAssignedMoversByOrderId(Long orderId);

    // 根据订单ID统计关联的搬运工人数量
    @Select("SELECT COUNT(*) FROM order_mover WHERE order_id = #{orderId}")
    int countByOrderId(Long orderId);

    @Insert("INSERT INTO order_mover (order_id, mover_id) VALUES (#{orderId}, #{moverId})")
    void insert(Long orderId, Long moverId);

    //  根据订单ID统计已分配的搬家工人数量
    @Select("SELECT COUNT(id) FROM order_mover WHERE order_id = #{orderId}")
    int countAssignedMoversByOrderId(Long orderId);

    @Select("SELECT EXISTS(SELECT 1 FROM order_mover WHERE order_id = #{orderId} AND mover_id = #{moverId})")
    boolean existsByOrderIdAndMoverId(Long orderId, Long moverId);

    // 根据订单ID和搬运工ID删除关联记录 (用于取消分配)
    @Delete("DELETE FROM order_mover WHERE order_id = #{orderId} AND mover_id = #{moverId}")
    int deleteByOrderIdAndMoverId(Long orderId, Long moverId);

}