package com.ahut.controller.back;

import com.ahut.properties.JwtProperties;
import com.ahut.service.AdminService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/back/admin")
@Slf4j
public class AdminController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private JwtProperties jwtProperties;

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
//    /**
//     * 新增员工
//     *
//     * @param employeeDTO
//     * @return
//     */
//    @PostMapping
//    @ApiOperation("新增员工")
//    public Result save(@RequestBody EmployeeDTO employeeDTO) {
//        log.info("新增员工:{}", employeeDTO);
//        System.out.println("当前线程id：" + Thread.currentThread().getId());
//        employeeService.save(employeeDTO);
//        return Result.success();
//    }
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
