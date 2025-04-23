package com.***REMOVED***.mapper;

import com.***REMOVED***.entity.Rating;
import com.***REMOVED***.vo.CustomerRatingVO;
import com.***REMOVED***.vo.ServiceRatingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RatingMapper {

    // 批量插入评价记录
    // @AutoFill(OperationType.INSERT)  // @AutoFill 通常应用于单条记录插入
    void insertBatch(List<Rating> ratings);


    /**
     * 根据订单ID查询评价记录列表
     *
     * @param orderId 订单ID
     * @return 评价实体对象列表
     */
//    @Select("select id, order_id, customer_id, ratee_id, rating_type, rating_value, comment, rating_time, create_time, update_time " +
//            "from rating where order_id = #{orderId}")
    //List<Rating> getByOrderId(@Param("orderId") Long orderId);

    // 查询特定订单、评分类型、被评分者的评价记录，用于判断是否已评价
    @Select("SELECT id, order_id, customer_id, ratee_id, rating_type, rating_value, comment, rating_time, " +
            "create_time, update_time " +
            "FROM rating WHERE order_id = #{orderId} AND rating_type = #{ratingType} AND ratee_id = #{rateeId}")
    Rating getByOrderIdAndTypeAndRateeId(Long orderId, String ratingType, Long rateeId);


    // 根据服务项ID获取用户评价列表，包含评价人姓名
    List<ServiceRatingVO> getServiceRatingsByServiceId(Long serviceId);

    // 根据用户ID查询该用户提交的历史评价记录，包含关联信息
    List<CustomerRatingVO> getCustomerRatingByCustomerId(Long customerId);


    // 其他可能的查询方法，例如根据客户ID查询评价列表，查询某个司机/搬运工人的平均评分等
}