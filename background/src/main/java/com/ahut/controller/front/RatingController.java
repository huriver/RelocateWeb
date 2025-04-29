package com.***REMOVED***.controller.front;

import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.OverallRatingSubmitDTO;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.RatingService;
import com.***REMOVED***.vo.CustomerRatingVO;
import com.***REMOVED***.vo.ServiceRatingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户端评价接口
 */
@RestController
@RequestMapping("/front/rating")
@Slf4j
public class RatingController {

    @Autowired
    private RatingService ratingService;

    /**
     * 根据消费者ID查询其历史评价记录，包含订单和服务信息
     *
     * @return
     */
    @GetMapping("/history")
    public Result<List<CustomerRatingVO>> getCustomerRatingHistory() {
        log.info("用户端用户{}，获取当前用户历史评价列表", BaseContext.getCurrentId());
        List<CustomerRatingVO> customerRatingVOList = ratingService.getCustomerRatingHistory(BaseContext.getCurrentId());
        return Result.success(customerRatingVOList);
    }

    /**
     * 用户提交订单评价 (多个评分项)
     *
     * @param overallRatingSubmitDTO
     * @return
     */
    @PostMapping("/review")
    public Result<String> submitRatings(@RequestBody OverallRatingSubmitDTO overallRatingSubmitDTO) {
        log.info("用户端提交订单评价，参数：{}", overallRatingSubmitDTO);
        ratingService.submitRatings(overallRatingSubmitDTO);
        return Result.success();
    }

    /**
     * 根据服务项ID获取服务评价列表
     *
     * @param serviceId
     * @return
     */
    @GetMapping("/service/{serviceId}")
    public Result<List<ServiceRatingVO>> getServiceRatings(@PathVariable Long serviceId) {
        log.info("用户端获取服务项 {} 的评价列表", serviceId);
        List<ServiceRatingVO> ratingList = ratingService.getServiceRatings(serviceId);
        return Result.success(ratingList);
    }

}