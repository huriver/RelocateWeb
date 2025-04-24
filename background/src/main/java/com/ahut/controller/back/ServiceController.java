package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.ServiceQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.ServiceService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController("backServiceController")
@RequestMapping("/back/service")
@Slf4j
public class ServiceController {
    @Autowired
    private ServiceService serviceService;

    /**
     * 后台端分页查询服务项列表 (带条件查询)
     *
     * @param serviceQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(ServiceQueryDTO serviceQueryDTO) {
        log.info("后台端服务项分页查询: {}", serviceQueryDTO);
        PageResult pageResult = serviceService.pageQueryByAdmin(serviceQueryDTO);
        return Result.success(pageResult); // 返回包含所有字段+关联名称的分页结果
    }


}
