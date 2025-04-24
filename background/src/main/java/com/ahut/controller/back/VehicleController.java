package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.VehiclePageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.VehicleService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/back/vehicle")
@Slf4j
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;


    /**
     * 分页查询车辆列表 (带条件查询)
     *
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(VehiclePageQueryDTO pageQueryDTO) {
        log.info("车辆分页查询: {}", pageQueryDTO);
        PageResult pageResult = vehicleService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

}