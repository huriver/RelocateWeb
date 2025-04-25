package com.***REMOVED***.controller.back;

import com.***REMOVED***.dto.MovingNewsDTO;
import com.***REMOVED***.dto.MovingNewsPageQueryDTO;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.MovingNewsService;
import com.***REMOVED***.vo.MovingNewsVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


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
     * 新增搬家新闻
     *
     * @param movingNewsDTO
     * @return
     */
    @PostMapping
    public Result save(@RequestBody MovingNewsDTO movingNewsDTO) {
        log.info("后台端新增搬家新闻: {}", movingNewsDTO);
        movingNewsService.save(movingNewsDTO);
        return Result.success();
    }

    /**
     * 根据ID查询搬家新闻详情 (用于回显)
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public Result<MovingNewsVO> getById(@PathVariable Long id) {
        log.info("后台端根据ID查询搬家新闻详情: {}", id);
        MovingNewsVO movingNewsVO = movingNewsService.getByIdByAdmin(id);
        return Result.success(movingNewsVO);
    }

    /**
     * 修改搬家新闻
     *
     * @param movingNewsDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody MovingNewsDTO movingNewsDTO) {
        log.info("后台端修改搬家新闻: {}", movingNewsDTO);
        movingNewsService.update(movingNewsDTO);
        return Result.success();
    }

    /**
     * 根据ID删除搬家新闻
     *
     * @param id
     * @return
     */
    @DeleteMapping
    public Result deleteById(Long id) {
        log.info("后台端删除搬家新闻: {}", id);
        movingNewsService.deleteById(id);
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
