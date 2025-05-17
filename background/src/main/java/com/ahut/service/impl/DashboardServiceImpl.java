package com.***REMOVED***.service.impl; // 请替换为实际包名


import com.***REMOVED***.exception.BusinessException;
import com.***REMOVED***.mapper.DashboardMapper;
import com.***REMOVED***.service.DashboardService;
import com.***REMOVED***.vo.*;
import com.yourcompany.relocateweb.admin.dashboard.vo.DashboardBusinessDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 后台仪表盘服务实现类
 */
@Service
public class DashboardServiceImpl implements DashboardService {

    @Autowired
    private DashboardMapper dashboardMapper;

    /**
     * 获取核心运营数据概览
     *
     * @param startDate 统计开始日期 (可选)
     * @param endDate   统计结束日期 (可选)
     * @return 运营数据概览VO
     */
    @Override
    public DashboardBusinessDataVO getBusinessData(LocalDate startDate, LocalDate endDate) {
        // 1. 订单总数
        Long totalOrderCount = Optional.ofNullable(dashboardMapper.countOrders(startDate, endDate)).orElse(0L);

        // 2. 已完成订单数
        // 订单状态：0-待接单，1-司机已接单，等待搬运工人，2-已接单，3-进行中，4-已完成，5-已取消
        Long completedOrderCount = Optional.ofNullable(dashboardMapper.countOrdersByStatus(startDate, endDate, 4)).orElse(0L);

        // 3. 已取消订单数
        Long cancelledOrderCount = Optional.ofNullable(dashboardMapper.countOrdersByStatus(startDate, endDate, 5)).orElse(0L);

        // 4. 总营收
        BigDecimal totalRevenue = Optional.ofNullable(dashboardMapper.sumRevenue(startDate, endDate)).orElse(BigDecimal.ZERO);

        // 5. 用户总数 (消费者+司机+搬家工人)
        Long totalCustomerCount = Optional.ofNullable(dashboardMapper.countCustomers(startDate, endDate)).orElse(0L);
        Long totalDriverCount = Optional.ofNullable(dashboardMapper.countDrivers(startDate, endDate)).orElse(0L);
        Long totalMoverCount = Optional.ofNullable(dashboardMapper.countMovers(startDate, endDate)).orElse(0L);
        Long totalUserCount = totalCustomerCount + totalDriverCount + totalMoverCount;

        // 6. 已完成订单平均价格
        BigDecimal averageOrderPrice = Optional.ofNullable(dashboardMapper.calculateAverageOrderPrice(startDate, endDate)).orElse(BigDecimal.ZERO);


        // 使用Builder模式构建VO对象
        return DashboardBusinessDataVO.builder()
                .totalOrderCount(totalOrderCount)
                .completedOrderCount(completedOrderCount)
                .cancelledOrderCount(cancelledOrderCount)
                .totalRevenue(totalRevenue)
                .totalUserCount(totalUserCount)
                .totalCustomerCount(totalCustomerCount)
                .totalDriverCount(totalDriverCount)
                .totalMoverCount(totalMoverCount)
                .averageOrderPrice(averageOrderPrice)
                .build();
    }


    /**
     * 获取订单趋势数据
     *
     * @param startDate 统计开始日期
     * @param endDate   统计结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @return 订单趋势数据列表
     */
    @Override
    public List<OrderTrendVO> getOrderTrend(LocalDate startDate, LocalDate endDate, String timeUnit) {
        List<Map<String, Object>> trendData = dashboardMapper.countOrdersByTime(startDate, endDate, timeUnit);

        // 将 List<Map<String, Object>> 转换为 List<OrderTrendVO>
        return trendData.stream().map(map -> OrderTrendVO.builder()
                .dateLabel((String) map.get("dateLabel"))
                .count((Long) map.get("count"))
                .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取营收趋势数据
     *
     * @param startDate 统计开始日期
     * @param endDate   统计结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @return 营收趋势数据列表
     */
    @Override
    public List<RevenueTrendVO> getRevenueTrend(LocalDate startDate, LocalDate endDate, String timeUnit) {
        List<Map<String, Object>> trendData = dashboardMapper.sumRevenueByTime(startDate, endDate, timeUnit);

        // 将 List<Map<String, Object>> 转换为 List<RevenueTrendVO>
        return trendData.stream().map(map -> RevenueTrendVO.builder()
                .dateLabel((String) map.get("dateLabel"))
                .amount((BigDecimal) map.get("amount"))
                .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取用户增长趋势数据
     *
     * @param startDate 统计开始日期
     * @param endDate   统计结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @param userType  用户类型过滤 (可选, 'CUSTOMER', 'DRIVER', 'MOVER')
     * @return 用户增长趋势数据列表
     */
    @Override
    public List<UserGrowthVO> getUserGrowthTrend(LocalDate startDate, LocalDate endDate, String timeUnit, String userType) {
        String userTable = null;
        if ("CUSTOMER".equalsIgnoreCase(userType)) {
            userTable = "customer";
        } else if ("DRIVER".equalsIgnoreCase(userType)) {
            userTable = "driver";
        } else if ("MOVER".equalsIgnoreCase(userType)) {
            userTable = "mover";
        }
        // 如果userType为null或无效，可以统计总用户增长，或返回空列表，这里返回空列表表示未指定类型或类型无效
        if (userTable == null && userType != null) {
            return Collections.emptyList(); // 返回空列表如果指定了无效类型
        }

        // 为了简化当前实现，我们要求 userType 必须是 CUSTOMER, DRIVER, MOVER 之一
        if (userTable == null) {
            // 如果userType是null，您可以在这里决定是抛异常还是返回总用户增长数据
            // 假设userType是必传的，或者不传代表无效
            // 如果用户不传userType，并且您希望统计总用户，Mapper需要使用union all
            // 当前Mapper设计只支持查单表，所以我们要求userType必须有效
            throw new BusinessException("用户增长趋势的 userType 无效或缺失。");
        }


        List<Map<String, Object>> trendData = dashboardMapper.countUsersByTime(startDate, endDate, timeUnit, userTable);

        // 将 List<Map<String, Object>> 转换为 List<UserGrowthVO>
        return trendData.stream().map(map -> UserGrowthVO.builder()
                .dateLabel((String) map.get("dateLabel"))
                .count((Long) map.get("count"))
                // .userType(userType) // 如果VO需要userType字段，可以在此设置
                .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取订单状态分布数据
     *
     * @param startDate 统计开始日期 (可选)
     * @param endDate   统计结束日期 (可选)
     * @return 订单状态分布数据列表
     */
    @Override
    public List<OrderStatusDistributionVO> getOrderStatusDistribution(LocalDate startDate, LocalDate endDate) {
        List<Map<String, Object>> distributionData = dashboardMapper.countOrdersByStatusDistribution(startDate, endDate);

        // 获取该时间范围内的订单总数，用于计算百分比
        Long totalOrderCount = Optional.ofNullable(dashboardMapper.countOrders(startDate, endDate)).orElse(0L);

        List<OrderStatusDistributionVO> result = new ArrayList<>();
        // Map 订单状态码到名称
        Map<Integer, String> statusNameMap = new HashMap<>();
        statusNameMap.put(0, "待接单");
        statusNameMap.put(1, "司机已接单，等待搬运工人");
        statusNameMap.put(2, "已接单（团队已确认）");
        statusNameMap.put(3, "进行中");
        statusNameMap.put(4, "已完成");
        statusNameMap.put(5, "已取消");


        for (Map<String, Object> map : distributionData) {
            Integer status = (Integer) map.get("status");
            Long count = (Long) map.get("count");

            BigDecimal percentage = BigDecimal.ZERO;
            if (totalOrderCount != null && totalOrderCount > 0 && count != null) {
                percentage = BigDecimal.valueOf(count)
                        .divide(BigDecimal.valueOf(totalOrderCount), 4, RoundingMode.HALF_UP) // 计算百分比，保留4位小数
                        .multiply(BigDecimal.valueOf(100)); // 乘以100表示百分比
            }

            result.add(OrderStatusDistributionVO.builder()
                    .status(status)
                    .statusName(statusNameMap.getOrDefault(status, "未知状态")) // 获取状态名称
                    .count(count)
                    .percentage(percentage)
                    .build());
        }

        return result;
    }

    /**
     * 获取服务/货车类型使用分布数据
     *
     * @param startDate 统计开始日期 (可选)
     * @param endDate   统计结束日期 (可选)
     * @param type      资源类型 ('SERVICE' 或 'TRUCK_TYPE')
     * @return 服务/货车类型使用分布数据列表
     */
    @Override
    public List<ServiceResourceDistributionVO> getServiceResourceDistribution(LocalDate startDate, LocalDate endDate, String type) {
        // 参数校验，确保type是 SERVICE 或 TRUCK_TYPE
        if (!"SERVICE".equalsIgnoreCase(type) && !"TRUCK_TYPE".equalsIgnoreCase(type)) {
            return Collections.emptyList(); // 返回空列表或抛出异常
        }

        List<Map<String, Object>> distributionData = dashboardMapper.countServiceResourcesByTypeDistribution(startDate, endDate, type.toUpperCase());

        // 将 List<Map<String, Object>> 转换为 List<ServiceResourceDistributionVO>
        return distributionData.stream().map(map -> ServiceResourceDistributionVO.builder()
                .name((String) map.get("name"))
                .count((Long) map.get("count"))
                .build())
                .collect(Collectors.toList());
    }

    /**
     * 获取评分分布数据
     *
     * @param startDate  统计开始日期 (可选)
     * @param endDate    统计结束日期 (可选)
     * @param ratingType 评分类型过滤 (可选, '司机', '搬运工人', '服务')
     * @return 评分分布数据列表
     */
    @Override
    public List<RatingDistributionVO> getRatingDistribution(LocalDate startDate, LocalDate endDate, String ratingType) {
        List<Map<String, Object>> distributionData = dashboardMapper.countRatingsByValueDistribution(startDate, endDate, ratingType);

        // 获取该时间范围和过滤条件下的评价总数，用于计算百分比
        Long totalRatingCount = Optional.ofNullable(dashboardMapper.countRatings(startDate, endDate, ratingType)).orElse(0L);


        List<RatingDistributionVO> result = new ArrayList<>();
        for (Map<String, Object> map : distributionData) {
            Integer ratingValue = (Integer) map.get("ratingValue");
            Long count = (Long) map.get("count");

            BigDecimal percentage = BigDecimal.ZERO;
            if (totalRatingCount != null && totalRatingCount > 0 && count != null) {
                percentage = BigDecimal.valueOf(count)
                        .divide(BigDecimal.valueOf(totalRatingCount), 4, RoundingMode.HALF_UP) // 计算百分比
                        .multiply(BigDecimal.valueOf(100)); // 乘以100表示百分比
            }

            result.add(RatingDistributionVO.builder()
                    .ratingValue(ratingValue)
                    .count(count)
                    .percentage(percentage)
                    .build());
        }

        return result;
    }

    /**
     * 统计指定时间范围内的总评价数量
     *
     * @param startDate  开始日期 (可选)
     * @param endDate    结束日期 (可选)
     * @param ratingType 评分类型过滤 (可选)
     * @return 总评价数量
     */
    @Override
    public Long countRatings(LocalDate startDate, LocalDate endDate, String ratingType) {
        // Service层的这个方法只是简单转发给Mapper，实际调用在其他方法中
        return dashboardMapper.countRatings(startDate, endDate, ratingType);
    }

    /**
     * 获取评分靠前的司机列表
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 司机排名列表
     */
    @Override
    public List<DriverRankingVO> getTopDriversByRating(int limit, int minRatingCount) {
        return dashboardMapper.findTopDriversByRating(limit, minRatingCount);
    }

    /**
     * 获取评分靠前的搬家工人列表
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 搬家工人排名列表
     */
    @Override
    public List<MoverRankingVO> getTopMoversByRating(int limit, int minRatingCount) {
        return dashboardMapper.findTopMoversByRating(limit, minRatingCount);
    }

    /**
     * 获取评分靠前的服务项列表
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 服务项排名列表
     */
    @Override
    public List<ServiceRankingVO> getTopServicesByRating(int limit, int minRatingCount) {
        return dashboardMapper.findTopServicesByRating(limit, minRatingCount);
    }
}