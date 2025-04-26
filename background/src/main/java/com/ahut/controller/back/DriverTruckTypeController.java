package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.DriverTruckTypePageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.DriverTruckTypeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/back/driverTruckType")
@Slf4j
public class DriverTruckTypeController {

    @Autowired
    private DriverTruckTypeService driverTruckTypeService;

    /**
     * 分页查询司机的可驾驶货车类型列表 (带条件查询)
     *
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DriverTruckTypePageQueryDTO pageQueryDTO) {
        log.info("后台端司机资质分页查询: {}", pageQueryDTO);
        PageResult pageResult = driverTruckTypeService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增司机的可驾驶货车类型
     *
     * @param vehicleDTO
     * @return
     */
//    @PostMapping
//    public Result save(@RequestBody VehicleDTO vehicleDTO) {
//        log.info("后台端新增司机的可驾驶货车类型: {}", vehicleDTO);
//        vehicleService.save(vehicleDTO);
//        return Result.success();
//    }
//
//    /**
//     * 根据ID查询司机的可驾驶货车类型详情 (用于回显)
//     *
//     * @param id
//     * @return
//     */
//    @GetMapping("/{id}")
//    public Result<VehicleVO> getById(@PathVariable Long id) {
//        log.info("后台端根据ID查询司机的可驾驶货车类型详情: {}", id);
//        VehicleVO vehicleVO = vehicleService.getByIdByAdmin(id);
//        return Result.success(vehicleVO);
//    }
//
//    /**
//     * 修改司机的可驾驶货车类型
//     *
//     * @param vehicleDTO
//     * @return
//     */
//    @PutMapping
//    public Result update(@RequestBody VehicleDTO vehicleDTO) {
//        log.info("后台端修改司机的可驾驶货车类型: {}", vehicleDTO);
//        vehicleService.update(vehicleDTO);
//        return Result.success();
//    }
//
//    /**
//     * 根据ID删除司机的可驾驶货车类型
//     *
//     * @param id
//     * @return
//     */
//    @DeleteMapping
//    public Result deleteById(Long id) {
//        log.info("后台端删除司机的可驾驶货车类型: {}", id);
//        vehicleService.deleteById(id);
//        return Result.success();
//    }

}