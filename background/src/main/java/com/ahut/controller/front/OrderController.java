package com.***REMOVED***.controller.front;

import com.***REMOVED***.dto.PriceEstimationDTO;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.vo.PriceEstimationResultVO;
import com.***REMOVED***.service.OrderService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/front/order")
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;


    /**
     * 订单价格估算接口
     *
     * @param estimationDTO
     * @return
     */
    @PostMapping("/estimate")
    public Result<PriceEstimationResultVO> estimatePrice(@RequestBody PriceEstimationDTO estimationDTO) {
        log.info("用户端估算搬家订单价格接口调用，参数：{}", estimationDTO);
        PriceEstimationResultVO priceEstimationResultVO = orderService.estimatePrice(estimationDTO);
        return Result.success(priceEstimationResultVO);
    }

    // Future: 订单提交接口方法框架
    /*
    @PostMapping("/submit")
    public Result<OrderSubmitVO> submitOrder(@RequestBody OrderSubmitDTO submitDTO) {
         log.info("用户提交订单接口调用，参数：{}", submitDTO);
         OrderSubmitVO submitResultVO = orderService.submitOrder(submitDTO);
         return Result.success(submitResultVO);
    }
    */
}