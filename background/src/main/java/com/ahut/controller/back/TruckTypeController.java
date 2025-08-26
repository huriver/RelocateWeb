package com.ahut.controller.back;

import com.ahut.dto.TruckTypeDTO;
import com.ahut.dto.TruckTypePageQueryDTO;
import com.ahut.entity.TruckType;
import com.ahut.result.PageResult;
import com.ahut.result.Result;
import com.ahut.service.TruckTypeService;
import com.ahut.vo.TruckTypeVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


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
        log.info("后台端货车类型分页查询: {}", truckTypePageQueryDTO);
        PageResult pageResult = truckTypeService.pageQuery(truckTypePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询所有货车类型列表
     *
     * @return
     */
    @GetMapping("/list")
    public Result<List<TruckType>> list() {
        log.info("后台端查询所有货车类型列表");
        List<TruckType> truckTypeList = truckTypeService.list();
        return Result.success(truckTypeList);
    }

    /**
     * 新增货车类型
     *
     * @param truckTypeDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody TruckTypeDTO truckTypeDTO) {
        log.info("后台端新增货车类型: {}", truckTypeDTO);
        truckTypeService.save(truckTypeDTO);
        return Result.success();
    }

    /**
     * 根据ID查询货车类型详情 (用于回显)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<TruckTypeVO> getById(@PathVariable Long id) {
        log.info("后台端根据ID查询货车类型详情: {}", id);
        TruckTypeVO truckTypeVO = truckTypeService.getByIdByAdmin(id);
        return Result.success(truckTypeVO);
    }

    /**
     * 修改货车类型
     *
     * @param truckTypeDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody TruckTypeDTO truckTypeDTO) {
        log.info("后台端修改货车类型: {}", truckTypeDTO);
        truckTypeService.update(truckTypeDTO);
        return Result.success();
    }

    /**
     * 根据ID删除货车类型
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("后台端删除货车类型: {}", id);
        truckTypeService.deleteById(id);
        return Result.success();
    }

    /**
     * 根据司机ID查询可驾驶货车类型列表 (供前端下拉框使用)
     *
     * @param driverId
     * @return
     */
    @GetMapping("/byDriver/{driverId}")
    public Result<List<TruckType>> listByDriverId(@PathVariable Long driverId) {
        log.info("后台端根据司机ID查询可驾驶货车类型列表: {}", driverId);
        List<TruckType> truckTypeList = truckTypeService.listByDriverId(driverId);
        return Result.success(truckTypeList);
    }


}