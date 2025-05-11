package com.***REMOVED***.controller.back;


import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.DriverMyRatingPageQueryDTO;
import com.***REMOVED***.dto.MoverMyRatingPageQueryDTO;
import com.***REMOVED***.dto.RatingPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.RatingService;
import com.***REMOVED***.vo.DriverMyRatingDetailVO;
import com.***REMOVED***.vo.MoverMyRatingDetailVO;
import com.***REMOVED***.vo.RatingDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 管理端评分接口
 */
@RestController("backRatingController")
@RequestMapping("/back/rating")
@Slf4j
public class RatingController {

    @Autowired
    private RatingService ratingService;

    /**
     * 分页查询评分列表
     *
     * @param queryDTO 包含分页和过滤条件
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> pageQuery(RatingPageQueryDTO queryDTO) {
        log.info("分页查询评分列表：由管理员 {} 操作，参数：{}", BaseContext.getCurrentId(), queryDTO);
        PageResult pageResult = ratingService.pageQuery(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据 ID 查询单个评分详细信息
     *
     * @param id 评分记录ID (从路径变量获取)
     * @return 包含评分详细信息的 Result<RatingDetailVO>
     */
    @GetMapping("/{id}")
    public Result<RatingDetailVO> getRatingDetailsById(@PathVariable Long id) {
        log.info("查询单个评分详细信息，由管理员 {} 操作，查询ID：{}", BaseContext.getCurrentId(), id);
        RatingDetailVO detailVO = ratingService.getById(id);
        return Result.success(detailVO);
    }

    /**
     * 司机分页查询收到的评价列表
     *
     * @param driverMyRatingPageQueryDTO 查询条件DTO，作为方法参数接收查询字符串传参
     * @return 评价列表分页结果
     */
    @GetMapping("/driver/my-ratings")
    public Result<PageResult> driverPageQueryMyRatings(DriverMyRatingPageQueryDTO driverMyRatingPageQueryDTO) {
        log.info("司机{}，分页查询收到的评价：{}", BaseContext.getCurrentId(), driverMyRatingPageQueryDTO);
        PageResult pageResult = ratingService.driverPageQueryMyRatings(driverMyRatingPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 司机查询收到的指定评价详情
     *
     * @param id 评价记录的ID
     * @return 评价详情VO
     */
    @GetMapping("/driver/my-ratings/{id}")
    public Result<DriverMyRatingDetailVO> driverGetMyRatingDetail(@PathVariable Long id) {
        log.info("司机{}，查询司机收到的评价详情，评价ID：{}", BaseContext.getCurrentId(), id);
        DriverMyRatingDetailVO detailVO = ratingService.driverGetMyRatingDetail(id);
        return Result.success(detailVO);
    }

    /**
     * 搬家工人端：分页查询自己收到的评价
     *
     * @param queryDTO 查询参数 (Spring MVC会自动将请求参数映射到DTO的字段)
     * @return 分页结果
     */
    @GetMapping("/mover/my-ratings")
    public Result<PageResult> moverPageQueryMyRatings(MoverMyRatingPageQueryDTO queryDTO) {
        log.info("搬家工人{}，分页查询我的评价，参数：{}", BaseContext.getCurrentId(), queryDTO);
        PageResult pageResult = ratingService.moverPageQueryMyRatings(queryDTO);
        return Result.success(pageResult);
    }

    /**
     * 搬家工人端：查询我的评价详情
     *
     * @param ratingId 评价记录ID
     * @return 评价详情
     */
    @GetMapping("/mover/my-ratings/{ratingId}")
    public Result<MoverMyRatingDetailVO> moverGetMyRatingDetail(@PathVariable Long ratingId) {
        log.info("搬家工人{}，查询我的评价详情，评价ID：{}", BaseContext.getCurrentId(), ratingId);
        MoverMyRatingDetailVO detail = ratingService.moverGetMyRatingDetail(ratingId);
        return Result.success(detail);
    }

}