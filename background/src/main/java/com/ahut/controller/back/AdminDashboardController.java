package com.ahut.controller.back; // 请替换为实际包名，放在admin子包下更清晰


import com.ahut.result.Result;
import com.ahut.service.DashboardService;
import com.ahut.vo.*;
import com.yourcompany.relocateweb.admin.dashboard.vo.DashboardBusinessDataVO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

/**
 * 后台仪表盘数据接口 (管理员使用)
 */
@RestController
@RequestMapping("/back/dashboard")
public class AdminDashboardController {

    @Autowired
    private DashboardService dashboardService;

    /**
     * 获取核心运营数据概览 (卡片展示数据)
     *
     * @param startDate 统计开始日期 (可选, 格式: yyyy-MM-dd)
     * @param endDate   统计结束日期 (可选, 格式: yyyy-MM-dd)
     * @return 包含核心运营数据的Result对象
     */
    @GetMapping("/businessData")
    public Result<DashboardBusinessDataVO> getBusinessData(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {
        DashboardBusinessDataVO businessData = dashboardService.getBusinessData(startDate, endDate);
        return Result.success(businessData);
    }

    /**
     * 获取订单趋势数据
     *
     * @param startDate 统计开始日期 (格式:yyyy-MM-dd)
     * @param endDate   统计结束日期 (格式:yyyy-MM-dd)
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @return 订单趋势数据列表
     */
    @GetMapping("/orderTrend")
    public Result<List<OrderTrendVO>> getOrderTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam String timeUnit) { // timeUnit 可以考虑使用枚举

        List<OrderTrendVO> trendData = dashboardService.getOrderTrend(startDate, endDate, timeUnit);
        return Result.success(trendData);
    }

    /**
     * 获取营收趋势数据
     *
     * @param startDate 统计开始日期 (格式:yyyy-MM-dd)
     * @param endDate   统计结束日期 (格式:yyyy-MM-dd)
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @return 营收趋势数据列表
     */
    @GetMapping("/revenueTrend")
    public Result<List<RevenueTrendVO>> getRevenueTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam String timeUnit) { // timeUnit 可以考虑使用枚举

        List<RevenueTrendVO> trendData = dashboardService.getRevenueTrend(startDate, endDate, timeUnit);
        return Result.success(trendData);
    }

    /**
     * 获取用户增长趋势数据
     *
     * @param startDate 统计开始日期 (格式:yyyy-MM-dd)
     * @param endDate   统计结束日期 (格式:yyyy-MM-dd)
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @param userType  用户类型过滤 ('CUSTOMER', 'DRIVER', 'MOVER')
     * @return 用户增长趋势数据列表
     */
    @GetMapping("/userGrowthTrend")
    public Result<List<UserGrowthVO>> getUserGrowthTrend(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam String timeUnit, // timeUnit 可以考虑使用枚举
            @RequestParam String userType) { // userType 是可选参数

        List<UserGrowthVO> trendData = dashboardService.getUserGrowthTrend(startDate, endDate, timeUnit, userType);
        return Result.success(trendData);
    }

    /**
     * 获取订单状态分布数据
     *
     * @param startDate 统计开始日期 (可选, 格式:yyyy-MM-dd)
     * @param endDate   统计结束日期 (可选, 格式:yyyy-MM-dd)
     * @return 订单状态分布数据列表
     */
    @GetMapping("/orderStatusDistribution")
    public Result<List<OrderStatusDistributionVO>> getOrderStatusDistribution(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate) {

        List<OrderStatusDistributionVO> distributionData = dashboardService.getOrderStatusDistribution(startDate, endDate);
        return Result.success(distributionData);
    }

    /**
     * 获取服务/货车类型使用分布数据
     *
     * @param startDate 统计开始日期 (可选, 格式:yyyy-MM-dd)
     * @param endDate   统计结束日期 (可选, 格式:yyyy-MM-dd)
     * @param type      资源类型 ('SERVICE' 或 'TRUCK_TYPE')
     * @return 服务/货车类型使用分布数据列表
     */
    @GetMapping("/serviceResourceDistribution")
    public Result<List<ServiceResourceDistributionVO>> getServiceResourceDistribution(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam String type) {

        List<ServiceResourceDistributionVO> distributionData = dashboardService.getServiceResourceDistribution(startDate, endDate, type);
        return Result.success(distributionData);
    }

    /**
     * 获取评分分布数据
     *
     * @param startDate  统计开始日期 (可选, 格式:yyyy-MM-dd)
     * @param endDate    统计结束日期 (可选, 格式:yyyy-MM-dd)
     * @param ratingType 评分类型过滤 (可选, 'DRIVER', 'MOVER', 'SERVICE')
     * @return 评分分布数据列表
     */
    @GetMapping("/ratingDistribution")
    public Result<List<RatingDistributionVO>> getRatingDistribution(
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) String ratingType) {

        List<RatingDistributionVO> distributionData = dashboardService.getRatingDistribution(startDate, endDate, ratingType);
        return Result.success(distributionData);
    }

    /**
     * 获取评分靠前的司机列表
     *
     * @param limit          返回数量限制 (默认为10)
     * @param minRatingCount 最低评分次数要求 (默认为10)
     * @return 司机排名列表
     */
    @GetMapping("/topDriversByRating")
    public Result<List<DriverRankingVO>> getTopDriversByRating(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "10") int minRatingCount) {

        List<DriverRankingVO> rankingData = dashboardService.getTopDriversByRating(limit, minRatingCount);
        return Result.success(rankingData);
    }

    /**
     * 获取评分靠前的搬家工人列表
     *
     * @param limit          返回数量限制 (默认为10)
     * @param minRatingCount 最低评分次数要求 (默认为10)
     * @return 搬家工人排名列表
     */
    @GetMapping("/topMoversByRating")
    public Result<List<MoverRankingVO>> getTopMoversByRating(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "10") int minRatingCount) {

        List<MoverRankingVO> rankingData = dashboardService.getTopMoversByRating(limit, minRatingCount);
        return Result.success(rankingData);
    }

    /**
     * 获取评分靠前的服务项列表
     *
     * @param limit          返回数量限制 (默认为10)
     * @param minRatingCount 最低评分次数要求 (默认为10)
     * @return 服务项排名列表
     */
    @GetMapping("/topServicesByRating")
    public Result<List<ServiceRankingVO>> getTopServicesByRating(
            @RequestParam(required = false, defaultValue = "10") int limit,
            @RequestParam(required = false, defaultValue = "10") int minRatingCount) {

        List<ServiceRankingVO> rankingData = dashboardService.getTopServicesByRating(limit, minRatingCount);
        return Result.success(rankingData);
    }

}