package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.MovingNewsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController("backMovingNewsController")
@RequestMapping("/back/moving-news")
@Slf4j
public class MovingNewsController {

    @Autowired
    private MovingNewsService movingNewsService;


    /**
     * 分页查询搬家新闻列表 (带条件查询)
     *
     * @param pageQueryDTO
     * @return
     */
    @GetMapping("/page")
    public Result<PageResult> page(MovingNewsPageQueryDTO pageQueryDTO) {
        log.info("后台端搬家新闻分页查询: {}", pageQueryDTO);
        PageResult pageResult = movingNewsService.pageQueryByAdmin(pageQueryDTO);
        return Result.success(pageResult); // 返回包含所有字段+关联管理员姓名的分页结果
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
