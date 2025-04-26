package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.VehicleDTO;
import com.***REMOVED***.dto.VehiclePageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.VehicleService;
import com.***REMOVED***.vo.VehicleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
        log.info("后台端车辆分页查询: {}", pageQueryDTO);
        PageResult pageResult = vehicleService.pageQuery(pageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 新增车辆
     *
     * @param vehicleDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody VehicleDTO vehicleDTO) {
        log.info("后台端新增车辆: {}", vehicleDTO);
        vehicleService.save(vehicleDTO);
        return Result.success();
    }

    /**
     * 根据ID查询车辆详情 (用于回显)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<VehicleVO> getById(@PathVariable Long id) {
        log.info("后台端根据ID查询车辆详情: {}", id);
        VehicleVO vehicleVO = vehicleService.getByIdByAdmin(id);
        return Result.success(vehicleVO);
    }

    /**
     * 修改车辆
     *
     * @param vehicleDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody VehicleDTO vehicleDTO) {
        log.info("后台端修改车辆: {}", vehicleDTO);
        vehicleService.update(vehicleDTO);
        return Result.success();
    }

    /**
     * 根据ID删除车辆
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("后台端删除车辆: {}", id);
        vehicleService.deleteById(id);
        return Result.success();
    }

}