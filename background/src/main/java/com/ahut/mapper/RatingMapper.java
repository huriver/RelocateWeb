package com.***REMOVED***.mapper;

import com.***REMOVED***.dto.DriverMyRatingPageQueryDTO;
import com.***REMOVED***.dto.MoverMyRatingPageQueryDTO;
import com.***REMOVED***.dto.RatingPageQueryDTO;
import com.***REMOVED***.entity.Rating;
import com.***REMOVED***.vo.*;
import com.github.pagehelper.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface RatingMapper {

    // 批量插入评价记录
    void insertBatch(List<Rating> ratings);

    // 查询特定订单、评分类型、被评分者的评价记录，用于判断是否已评价
    @Select("SELECT id, order_id, customer_id, ratee_id, rating_type, rating_value, comment, rating_time, " +
            "create_time, update_time " +
            "FROM rating WHERE order_id = #{orderId} AND rating_type = #{ratingType} AND ratee_id = #{rateeId}")
    Rating getByOrderIdAndTypeAndRateeId(Long orderId, String ratingType, Long rateeId);

    // 根据服务项ID获取用户评价列表，包含评价人姓名
    List<ServiceRatingVO> getServiceRatingsByServiceId(Long serviceId);

    // 根据消费者ID查询其历史评价记录，包含订单和服务信息
    List<CustomerRatingVO> getCustomerRatingByCustomerId(Long customerId);

    // 统计关联到特定服务项的评价数量
    @Select("SELECT COUNT(*) FROM rating WHERE rating_type = 'SERVICE' AND ratee_id = #{serviceId}")
    Integer countByServiceId(Long serviceId);

    // 分页查询评分列表 (返回 VO，包含关联信息)
    Page<RatingListVO> pageQuery(RatingPageQueryDTO queryDTO);

    // 根据ID查询单个评分记录 (只查询 rating 表自身的字段)
    @Select("select id, order_id, customer_id, ratee_id, rating_type, rating_value, comment, rating_time, " +
            "create_time, update_time from rating where id = #{id}")
    Rating getById(Long id);

    // 根据订单ID查询相关的评价列表，在SQL中获取被评价者名称和评价类型标签
    List<RatingVO> getRatingsByOrderId(Long orderId, String ratingTypeDriver,
                                       String ratingTypeMover, String ratingTypeService);

    // 司机分页查询收到的评价列表
    Page<DriverMyRatingListVO> driverPageQueryMyRatings(DriverMyRatingPageQueryDTO dto, Long currentDriverId);

    // 司机查询收到的指定评价详情
    DriverMyRatingDetailVO driverGetMyRatingDetail(Long ratingId, Long driverId);

    // 搬家工人端：分页查询自己收到的评价列表
    Page<MoverMyRatingListVO> moverPageQueryMyRatings(MoverMyRatingPageQueryDTO queryDTO, Long moverId);

    // 搬家工人端：查询我的评价详情
    MoverMyRatingDetailVO moverGetMyRatingDetail(Long ratingId, Long moverId);

}