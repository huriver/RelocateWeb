package com.ahut.service; // 请替换为实际包名

import com.ahut.vo.*;
import com.yourcompany.relocateweb.admin.dashboard.vo.DashboardBusinessDataVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 后台仪表盘服务接口
 */
public interface DashboardService {

    /**
     * 获取核心运营数据概览
     *
     * @param startDate 统计开始日期 (可选)
     * @param endDate   统计结束日期 (可选)
     * @return 运营数据概览VO
     */
    DashboardBusinessDataVO getBusinessData(LocalDate startDate, LocalDate endDate);

    /**
     * 获取订单趋势数据
     *
     * @param startDate 统计开始日期
     * @param endDate   统计结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @return 订单趋势数据列表
     */
    List<OrderTrendVO> getOrderTrend(LocalDate startDate, LocalDate endDate, String timeUnit);

    /**
     * 获取营收趋势数据
     *
     * @param startDate 统计开始日期
     * @param endDate   统计结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @return 营收趋势数据列表
     */
    List<RevenueTrendVO> getRevenueTrend(LocalDate startDate, LocalDate endDate, String timeUnit);

    /**
     * 获取用户增长趋势数据
     *
     * @param startDate 统计开始日期
     * @param endDate   统计结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @param userType  用户类型过滤 (可选, 'CUSTOMER', 'DRIVER', 'MOVER')
     * @return 用户增长趋势数据列表
     */
    List<UserGrowthVO> getUserGrowthTrend(LocalDate startDate, LocalDate endDate, String timeUnit, String userType);

    /**
     * 获取订单状态分布数据
     *
     * @param startDate 统计开始日期 (可选)
     * @param endDate   统计结束日期 (可选)
     * @return 订单状态分布数据列表
     */
    List<OrderStatusDistributionVO> getOrderStatusDistribution(LocalDate startDate, LocalDate endDate);

    /**
     * 获取服务/货车类型使用分布数据
     *
     * @param startDate 统计开始日期 (可选)
     * @param endDate   统计结束日期 (可选)
     * @param type      资源类型 ('SERVICE' 或 'TRUCK_TYPE')
     * @return 服务/货车类型使用分布数据列表
     */
    List<ServiceResourceDistributionVO> getServiceResourceDistribution(LocalDate startDate, LocalDate endDate, String type);

    /**
     * 获取评分分布数据
     *
     * @param startDate  统计开始日期 (可选)
     * @param endDate    统计结束日期 (可选)
     * @param ratingType 评分类型过滤 (可选, '司机', '搬运工人', '服务')
     * @return 评分分布数据列表
     */
    List<RatingDistributionVO> getRatingDistribution(LocalDate startDate, LocalDate endDate, String ratingType);

    /**
     * 统计指定时间范围内的总评价数量
     *
     * @param startDate  开始日期 (可选)
     * @param endDate    结束日期 (可选)
     * @param ratingType 评分类型过滤 (可选)
     * @return 总评价数量
     */
    Long countRatings(LocalDate startDate, LocalDate endDate, String ratingType);

    /**
     * 获取评分靠前的司机列表
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 司机排名列表
     */
    List<DriverRankingVO> getTopDriversByRating(int limit, int minRatingCount);

    /**
     * 获取评分靠前的搬家工人列表
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 搬家工人排名列表
     */
    List<MoverRankingVO> getTopMoversByRating(int limit, int minRatingCount);

    /**
     * 获取评分靠前的服务项列表
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 服务项排名列表
     */
    List<ServiceRankingVO> getTopServicesByRating(int limit, int minRatingCount);
}