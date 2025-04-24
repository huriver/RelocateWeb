package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.ServiceCategoryPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.ServiceCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("backServiceCategoryController")
@RequestMapping("/back/serviceCategory")
@Slf4j
public class ServiceCategoryController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    /**
     * 分页查询服务类型列表 (带条件查询)
     *
     * @param serviceCategoryPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(ServiceCategoryPageQueryDTO serviceCategoryPageQueryDTO) {
        log.info("服务类型分页查询: {}", serviceCategoryPageQueryDTO);
        PageResult pageResult = serviceCategoryService.pageQuery(serviceCategoryPageQueryDTO);
        return Result.success(pageResult);
    }


}
