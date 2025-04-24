package com.***REMOVED***.controller.front;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.ServiceService;
import com.***REMOVED***.vo.ServiceDetailVO;
import com.***REMOVED***.vo.ServiceRatingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("frontServiceController")
@RequestMapping("/front/service")
@Slf4j
public class ServiceController {

    @Autowired
    private ServiceService serviceService;

    /**
     * 条件分页查询服务项列表
     *
     * @param
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(ServiceQueryDTO serviceQueryDTO) {
        log.info("用户端根据条件查询服务项列表：{}", serviceQueryDTO);
        PageResult pageResult = serviceService.pageQuery(serviceQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据服务项ID查询服务详情
     *
     * @param id
     * @return
     */
    @GetMapping("/serviceDetail/{id}")
    public Result<ServiceDetailVO> getServiceDetails(@PathVariable Long id) {
        log.info("用户端查询服务项详情：{}", id);
        ServiceDetailVO serviceDetailVO = serviceService.details(id);
        return Result.success(serviceDetailVO);
    }

    /**
     * 根据服务项ID获取服务评价列表
     *
     * @param serviceId
     * @return
     */
    @GetMapping("/ratings/{serviceId}")
    public Result<List<ServiceRatingVO>> getServiceRatings(@PathVariable Long serviceId) {
        log.info("用户端获取服务项 {} 的评价列表", serviceId);
        List<ServiceRatingVO> ratingList = serviceService.getServiceRatings(serviceId);
        return Result.success(ratingList);
    }


}
