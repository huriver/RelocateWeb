package com.ahut.controller.front;

import com.ahut.entity.ServiceCategory;
import com.ahut.result.Result;
import com.ahut.service.ServiceCategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;


@RestController
@RequestMapping("/front/service-category")
@Slf4j
public class ServiceCategoryController {

    @Autowired
    private ServiceCategoryService serviceCategoryService;

    /**
     * 查询所有服务类型
     *
     * @return
     */
    @GetMapping
    public Result<List<ServiceCategory>> listServiceCategories() {
        log.info("正在查询所有服务类型");
        List<ServiceCategory> serviceCategories = serviceCategoryService.listAll();
        return Result.success(serviceCategories);
    }

}
