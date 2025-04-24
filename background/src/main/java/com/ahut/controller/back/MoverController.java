package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.MoverPageQueryDTO;
import com.***REMOVED***.properties.JwtProperties;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.MoverService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/back/mover")
@Slf4j
public class MoverController {

    @Autowired
    private MoverService moverService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 搬家工人分页查询
     *
     * @param moverPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(MoverPageQueryDTO moverPageQueryDTO) {
        log.info("搬家工人分页查询，参数为:{}", moverPageQueryDTO);
        PageResult pageResult = moverService.pageQuery(moverPageQueryDTO);
        return Result.success(pageResult);
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
