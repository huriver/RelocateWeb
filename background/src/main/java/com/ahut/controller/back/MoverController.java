package com.ahut.controller.back;

import com.ahut.constant.JwtClaimsConstant;
import com.ahut.dto.MoverDTO;
import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Mover;
import com.ahut.properties.JwtProperties;
import com.ahut.result.Result;
import com.ahut.service.MoverService;
import com.ahut.utils.JwtUtil;
import com.ahut.vo.UserLoginVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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


    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("搬家工人登录：{}", userLoginDTO);
        Mover mover = moverService.login(userLoginDTO);

        //登录成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.ID, mover.getId());
        claims.put(JwtClaimsConstant.ROLE, JwtClaimsConstant.ROLE_MOVER);
        String token = JwtUtil.createJWT(
                jwtProperties.getBackSecretKey(),
                jwtProperties.getBackTtl(),
                claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(mover.getId())
                .username(mover.getUsername())
                .name(mover.getName())
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }


    @PostMapping
    public Result save(@RequestBody MoverDTO moverDTO) {
        log.info("新增搬家工人:{}", moverDTO);
        System.out.println("当前线程id：" + Thread.currentThread().getId());
        moverService.save(moverDTO);
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
