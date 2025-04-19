package com.ahut.controller.back;

import com.ahut.context.BaseContext;
import com.ahut.dto.ChangePasswordDTO;
import com.ahut.dto.DriverDTO;
import com.ahut.entity.Driver;
import com.ahut.result.Result;
import com.ahut.service.DriverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
     * 退出
     *
     * @return
     */
//    @PostMapping("/logout")
//    @ApiOperation("员工退出")
//    public Result<String> logout() {
//        return Result.success();
//    }
//

//
//    /**
//     * 员工分页查询
//     *
//     * @param employeePageQueryDTO
//     * @return
//     */
//    @GetMapping("/page")
//    @ApiOperation("员工分页查询")
//    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO) {
//        log.info("员工分页查询，参数为:{}", employeePageQueryDTO);
//        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
//        return Result.success(pageResult);
//    }
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
//    /**
//     * 根据id查询员工信息
//     *
//     * @param id
//     * @return
//     */
//    @GetMapping("/{id}")
//    @ApiOperation("根据id查询员工信息")
//    public Result<Employee> getById(@PathVariable long id) {
//        Employee employee = employeeService.getById(id);
//        return Result.success(employee);
//    }
//
//    /**
//     * 编辑员工信息
//     *
//     * @param employeeDTO
//     * @return
//     */
//    @PutMapping
//    @ApiOperation("编辑员工信息")
//    public Result update(@RequestBody EmployeeDTO employeeDTO) {
//        log.info("编辑员工信息:{}", employeeDTO);
//        employeeService.update(employeeDTO);
//        return Result.success();
//    }

}
