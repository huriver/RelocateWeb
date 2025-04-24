package com.***REMOVED***.controller.back;

import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.ChangePasswordDTO;
import com.***REMOVED***.dto.DriverDTO;
import com.***REMOVED***.dto.DriverPageQueryDTO;
import com.***REMOVED***.entity.Driver;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.DriverService;
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
        log.info("根据id查询司机信息:{}", id);
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
        log.info("编辑司机信息:{}", driverDTO);
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
        log.info("司机修改密码：{}", changePasswordDTO);
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
        log.info("司机分页查询，参数为:{}", driverPageQueryDTO);
        PageResult pageResult = driverService.pageQuery(driverPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 查询司机列表 (用于前端下拉框等，可根据状态筛选)
     *
     * @param isBanned
     * @return
     */
    @GetMapping("/list")
    public Result<List<Driver>> list(Boolean isBanned) {
        log.info("查询司机列表，状态筛选：{}", isBanned);
        List<Driver> driverList = driverService.list(isBanned);
        return Result.success(driverList);
    }

//
//    /**
//     * 启用禁用员工账号
//     *
//     * @param status
//     * @param id
//     * @return
//     */
//    @PostMapping("/status/{status}")
//    @ApiOperation("启用禁用员工账号")
//    public Result startOrStop(@PathVariable Integer status, long id) {
//        log.info("启用禁用员工账号:{},{}", status, id);
//        employeeService.startOrStop(status, id);
//        return Result.success();
//    }
//


}
