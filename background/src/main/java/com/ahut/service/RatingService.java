package com.***REMOVED***.service;

import com.***REMOVED***.dto.DriverMyRatingPageQueryDTO;
import com.***REMOVED***.dto.MoverMyRatingPageQueryDTO;
import com.***REMOVED***.dto.OverallRatingSubmitDTO;
import com.***REMOVED***.dto.RatingPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.vo.*;

import java.util.List;

public interface RatingService {

    // 根据消费者ID查询其历史评价记录，包含订单和服务信息
    List<CustomerRatingVO> getCustomerRatingHistory(Long customerId);

    // 用户提交订单评价 (多个评分项)
    void submitRatings(OverallRatingSubmitDTO overallRatingSubmitDTO);

    // 根据服务项ID获取用户评价列表，包含评价人姓名
    List<ServiceRatingVO> getServiceRatings(Long serviceId);

    // 分页查询评分列表
    PageResult pageQuery(RatingPageQueryDTO queryDTO);

    // 根据ID查询单个评分详细信息 (返回 VO，包含关联基本信息)
    RatingDetailVO getById(Long id);

    // 司机分页查询收到的评价列表
    PageResult driverPageQueryMyRatings(DriverMyRatingPageQueryDTO driverMyRatingPageQueryDTO);

    // 司机查询收到的指定评价详情
    DriverMyRatingDetailVO driverGetMyRatingDetail(Long id);

    // 搬家工人端：分页查询自己收到的评价
    PageResult moverPageQueryMyRatings(MoverMyRatingPageQueryDTO queryDTO);

    // 搬家工人端：查询我的评价详情
    MoverMyRatingDetailVO moverGetMyRatingDetail(Long ratingId);

}