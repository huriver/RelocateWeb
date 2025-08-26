package com.ahut.controller.front;

import com.ahut.dto.ServiceQueryDTO;
import com.ahut.result.PageResult;
import com.ahut.result.Result;
import com.ahut.service.ServiceService;
import com.ahut.vo.ServiceDetailVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

}
