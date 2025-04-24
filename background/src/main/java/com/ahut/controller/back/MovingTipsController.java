package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.MovingTipsPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.MovingTipsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("backMovingTipsController")
@RequestMapping("/back/moving-tips")
@Slf4j
public class MovingTipsController {

    @Autowired
    private MovingTipsService movingTipsService;

    /**
     * 分页查询搬家须知列表 (带条件查询)
     *
     * @param movingTipsPageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(MovingTipsPageQueryDTO movingTipsPageQueryDTO) {
        log.info("后台端搬家须知分页查询，参数为:{}", movingTipsPageQueryDTO);
        PageResult pageResult = movingTipsService.pageQueryByAdmin(movingTipsPageQueryDTO);
        return Result.success(pageResult);
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
