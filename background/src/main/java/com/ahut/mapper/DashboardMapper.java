package com.***REMOVED***.mapper; // 请替换为实际包名

import com.***REMOVED***.vo.DriverRankingVO;
import com.***REMOVED***.vo.MoverRankingVO;
import com.***REMOVED***.vo.ServiceRankingVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

/**
 * 后台仪表盘数据Mapper
 * SQL 语句将定义在同名的 DashboardMapper.xml 文件中
 */
@Mapper
public interface DashboardMapper {

    /**
     * 统计指定时间范围内的订单总数
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate   结束日期 (YYYY-MM-DD)
     * @return 订单总数
     */
    Long countOrders(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定时间范围和状态的订单数量
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate   结束日期 (YYYY-MM-DD)
     * @param status    订单状态
     * @return 对应状态的订单数量
     */
    Long countOrdersByStatus(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate, @Param("status") Integer status);

    /**
     * 统计指定时间范围内已支付订单的总金额 (总营收)
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate   结束日期 (YYYY-MM-DD)
     * @return 总营收金额
     */
    BigDecimal sumRevenue(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定时间范围内的消费者总数
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate   结束日期 (YYYY-MM-DD)
     * @return 消费者总数
     */
    Long countCustomers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定时间范围内的司机总数
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate   结束日期 (YYYY-MM-DD)
     * @return 司机总数
     */
    Long countDrivers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定时间范围内的搬家工人总数
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate   结束日期 (YYYY-MM-DD)
     * @return 搬家工人总数
     */
    Long countMovers(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 统计指定时间范围内已完成且已支付订单的平均价格
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期 (YYYY-MM-DD)
     * @param endDate   结束日期 (YYYY-MM-DD)
     * @return 平均订单价格
     */
    BigDecimal calculateAverageOrderPrice(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 按时间粒度统计指定时间范围内的订单数量
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @return 包含日期标签和数量的Map列表
     */
    List<Map<String, Object>> countOrdersByTime(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("timeUnit") String timeUnit);

    /**
     * 按时间粒度统计指定时间范围内已支付订单的总金额 (总营收)
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @return 包含日期标签和金额的Map列表
     */
    List<Map<String, Object>> sumRevenueByTime(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("timeUnit") String timeUnit);

    /**
     * 按时间粒度统计指定时间范围内的用户增长数量
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param timeUnit  时间粒度 ('DAY', 'WEEK', 'MONTH')
     * @param userTable 用户表名 (例如: 'customer', 'driver', 'mover')
     * @return 包含日期标签和数量的Map列表
     */
    List<Map<String, Object>> countUsersByTime(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("timeUnit") String timeUnit,
            @Param("userTable") String userTable); // 动态表名参数


    /**
     * 统计指定时间范围内的订单状态分布数量
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 包含订单状态码和数量的Map列表
     */
    List<Map<String, Object>> countOrdersByStatusDistribution(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 统计指定时间范围内服务/货车类型的使用分布数量
     * SQL 在 XML 中实现
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @param type      资源类型 ('SERVICE' 或 'TRUCK_TYPE')
     * @return 包含资源名称和使用数量的Map列表
     */
    List<Map<String, Object>> countServiceResourcesByTypeDistribution(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("type") String type);

    /**
     * 统计指定时间范围和评分类型下的评分值分布数量
     * SQL 在 XML 中实现
     *
     * @param startDate  开始日期
     * @param endDate    结束日期
     * @param ratingType 评分类型过滤 (可选)
     * @return 包含评分值和数量的Map列表
     */
    List<Map<String, Object>> countRatingsByValueDistribution(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("ratingType") String ratingType);

    /**
     * 统计指定时间范围和评分类型下的总评价数量
     * SQL 在 XML 中实现
     *
     * @param startDate  开始日期 (可选)
     * @param endDate    结束日期 (可选)
     * @param ratingType 评分类型过滤 (可选)
     * @return 总评价数量
     */
    Long countRatings(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate,
            @Param("ratingType") String ratingType);

    /**
     * 查找评分靠前的司机列表
     * SQL 在 XML 中实现
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 司机排名列表
     */
    List<DriverRankingVO> findTopDriversByRating(
            @Param("limit") int limit,
            @Param("minRatingCount") int minRatingCount);

    /**
     * 查找评分靠前的搬家工人列表
     * SQL 在 XML 中实现
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 搬家工人排名列表
     */
    List<MoverRankingVO> findTopMoversByRating(
            @Param("limit") int limit,
            @Param("minRatingCount") int minRatingCount);

    /**
     * 查找评分靠前的服务项列表
     * SQL 在 XML 中实现
     *
     * @param limit          返回数量限制
     * @param minRatingCount 最低评分次数要求
     * @return 服务项排名列表
     */
    List<ServiceRankingVO> findTopServicesByRating(
            @Param("limit") int limit,
            @Param("minRatingCount") int minRatingCount);
}