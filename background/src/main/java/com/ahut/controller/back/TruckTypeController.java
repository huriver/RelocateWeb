package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.TruckTypePageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.TruckTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/back/truckType")
@Slf4j
public class TruckTypeController {

    @Autowired
    private TruckTypeService truckTypeService;


    /**
     * 分页查询货车类型列表 (带条件查询)
     *
     * @param truckTypePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(TruckTypePageQueryDTO truckTypePageQueryDTO) {
        log.info("货车类型分页查询: {}", truckTypePageQueryDTO);
        PageResult pageResult = truckTypeService.pageQuery(truckTypePageQueryDTO);
        return Result.success(pageResult);
    }

}