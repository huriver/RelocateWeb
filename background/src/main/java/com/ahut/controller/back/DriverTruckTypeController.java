package com.ahut.controller.back;

import com.ahut.dto.DriverTruckTypeBatchDTO;
import com.ahut.dto.DriverTruckTypePageQueryDTO;
import com.ahut.result.PageResult;
import com.ahut.result.Result;
import com.ahut.service.DriverTruckTypeService;
import com.ahut.vo.DriverTruckTypeRelationVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/back/driverTruckType")
@Slf4j
public class DriverTruckTypeController {

    @Autowired
    private DriverTruckTypeService driverTruckTypeService;

    /**
     * 管理员分页查询司机的可驾驶货车类型列表 (司机中心的展示，带条件查询)
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
     * 批量新增司机的可驾驶货车类型关联
     *
     * @param driverTruckTypeBatchDTO 包含 driverId 和 truckTypeIds 列表的DTO
     * @return
     */
    @PostMapping
    public Result saveBatch(@RequestBody DriverTruckTypeBatchDTO driverTruckTypeBatchDTO) {
        log.info("后台端批量新增司机的可驾驶货车类型关联: 司机ID={}, 货车类型ID列表={}",
                driverTruckTypeBatchDTO.getDriverId(),
                driverTruckTypeBatchDTO.getTruckTypeIds());
        driverTruckTypeService.addDriverTruckTypesBatch(driverTruckTypeBatchDTO);
        return Result.success();
    }

    /**
     * 根据司机ID获取修改关联时的回显数据
     *
     * @param driverId
     * @return
     */
    @GetMapping("/{driverId}")
    public Result<DriverTruckTypeRelationVO> getByDriverId(@PathVariable Long driverId) {
        log.info("后台端获取司机 {} 的可驾驶货车类型关联回显数据", driverId);
        DriverTruckTypeRelationVO relationVO = driverTruckTypeService.getByDriverId(driverId);
        return Result.success(relationVO);
    }

    /**
     * 修改司机的可驾驶货车类型关联 (批量更新)
     * 接收包含司机ID和修改后最终的货车类型ID列表的DTO
     *
     * @param driverTruckTypeBatchDTO 包含 driverId 和修改后最终的 truckTypeIds 列表的DTO
     * @return 成功结果
     */
    @PutMapping
    public Result updateBatch(@RequestBody DriverTruckTypeBatchDTO driverTruckTypeBatchDTO) {
        log.info("后台端修改司机 {} 的可驾驶货车类型关联", driverTruckTypeBatchDTO.getDriverId());
        driverTruckTypeService.updateDriverTruckTypesBatch(driverTruckTypeBatchDTO);
        return Result.success();
    }

    /**
     * 根据司机ID删除其所有可驾驶货车类型关联
     *
     * @param driverId 司机的ID
     * @return 成功结果
     */
    @DeleteMapping
    public Result deleteAllByDriverId(Long driverId) {
        log.info("后台端删除司机 {} 的所有可驾驶货车类型关联", driverId);
        driverTruckTypeService.deleteByDriverId(driverId);
        return Result.success();
    }

}