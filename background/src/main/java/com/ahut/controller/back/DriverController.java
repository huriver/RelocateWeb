package com.***REMOVED***.controller.back;

import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.ChangePasswordDTO;
import com.***REMOVED***.dto.DriverDTO;
import com.***REMOVED***.dto.DriverPageQueryDTO;
import com.***REMOVED***.entity.Driver;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.DriverService;
import com.***REMOVED***.vo.DriverTypeVehicleVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/back/driver")
@Slf4j
public class DriverController {

    @Autowired
    private DriverService driverService;


    /**
     * 根据id查询司机信息
     *
     * @return
     */
    @GetMapping
    public Result<Driver> getById() {
        long id = BaseContext.getCurrentId();
        log.info("后台端根据id查询司机信息:{}", id);
        Driver driver = driverService.getById(id);
        return Result.success(driver);
    }


    /**
     * 编辑司机信息
     *
     * @param driverDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody DriverDTO driverDTO) {
        log.info("后台端编辑司机信息:{}", driverDTO);
        driverService.update(driverDTO);
        return Result.success();
    }

    /**
     * 修改密码
     *
     * @param changePasswordDTO
     * @return
     */
    @PutMapping("/editPassword")
    public Result changePassword(@RequestBody ChangePasswordDTO changePasswordDTO) {
        log.info("后台端司机修改密码：{}", changePasswordDTO);
        driverService.changePassword(changePasswordDTO);
        return Result.success();
    }

    /**
     * 司机分页查询
     *
     * @param driverPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(DriverPageQueryDTO driverPageQueryDTO) {
        log.info("后台端司机分页查询，参数为:{}", driverPageQueryDTO);
        PageResult pageResult = driverService.pageQuery(driverPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 根据姓名模糊查询司机列表 (供搜索下拉框使用)
     *
     * @param name
     * @return
     */
    @GetMapping("/list")
    public Result<List<Driver>> listByName(@RequestParam(required = false) String name) {
        log.info("后台端根据姓名模糊查询司机列表: {}", name);
        List<Driver> list = driverService.listByName(name);
        return Result.success(list);
    }

    /**
     * 封禁/解封司机账号
     *
     * @param isBanned 账号状态：0-解封，1-封禁
     * @param id       司机ID
     * @return
     */
    @PostMapping("/status/{isBanned}")
    public Result enableOrDisable(@PathVariable Integer isBanned, Long id) {
        log.info("封禁/解封司机账号：id={}, status={}", id, isBanned == 0 ? "解封" : "封禁");
        driverService.updateStatus(id, isBanned);
        return Result.success();
    }

    /**
     * 获取当前登录司机的可驾驭货车类型及对应的被分配车辆列表
     *
     * @return 包含司机能力与车辆组合列表的统一返回结果
     */
    @GetMapping("/type-vehicles")
    public Result<List<DriverTypeVehicleVO>> getDriverTypeVehicles() {
        log.info("司机{}，获取当前登录司机的可驾驭货车类型及对应的被分配车辆列表", BaseContext.getCurrentId());
        List<DriverTypeVehicleVO> capabilities = driverService.getDriverTypeVehicles();
        return Result.success(capabilities);
    }

}
