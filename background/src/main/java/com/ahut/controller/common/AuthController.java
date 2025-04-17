package com.ahut.controller.common;

import com.ahut.constant.JwtClaimsConstant;
import com.ahut.dto.UserLoginDTO;
import com.ahut.entity.Admin;
import com.ahut.entity.Customer;
import com.ahut.entity.Driver;
import com.ahut.entity.Mover;
import com.ahut.properties.JwtProperties;
import com.ahut.result.Result;
import com.ahut.service.AdminService;
import com.ahut.service.CustomerService;
import com.ahut.service.DriverService;
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

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthController {

    @Autowired
    private AdminService adminService;
    @Autowired
    private DriverService driverService;
    @Autowired
    private MoverService moverService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private JwtProperties jwtProperties;

    @PostMapping("/login")
    public Result<UserLoginVO> login(@RequestBody UserLoginDTO userLoginDTO) {
        log.info("用户登录：{}", userLoginDTO);
        String role = userLoginDTO.getRole(); // 从 UserLoginDTO 中获取 role
        Object user = null;
        String jwtSecretKey = null;
        long ttl = 0;

        Map<String, Object> claims = new HashMap<>();

        switch (role) {
            case JwtClaimsConstant.ROLE_ADMIN:
                Admin admin = adminService.login(userLoginDTO);
                user = admin;
                claims.put(JwtClaimsConstant.ID, admin.getId());
                claims.put(JwtClaimsConstant.ROLE, JwtClaimsConstant.ROLE_ADMIN);
                jwtSecretKey = jwtProperties.getBackSecretKey();
                ttl = jwtProperties.getBackTtl();
                break;
            case JwtClaimsConstant.ROLE_DRIVER:
                Driver driver = driverService.login(userLoginDTO);
                user = driver;
                claims.put(JwtClaimsConstant.ID, driver.getId());
                claims.put(JwtClaimsConstant.ROLE, JwtClaimsConstant.ROLE_DRIVER);
                jwtSecretKey = jwtProperties.getBackSecretKey();
                ttl = jwtProperties.getBackTtl();
                break;
            case JwtClaimsConstant.ROLE_MOVER:
                Mover mover = moverService.login(userLoginDTO);
                user = mover;
                claims.put(JwtClaimsConstant.ID, mover.getId());
                claims.put(JwtClaimsConstant.ROLE, JwtClaimsConstant.ROLE_MOVER);
                jwtSecretKey = jwtProperties.getBackSecretKey();
                ttl = jwtProperties.getBackTtl();
                break;
            case JwtClaimsConstant.ROLE_CUSTOMER:
                Customer customer = customerService.login(userLoginDTO);
                user = customer;
                claims.put(JwtClaimsConstant.ID, customer.getId());
                claims.put(JwtClaimsConstant.ROLE, JwtClaimsConstant.ROLE_CUSTOMER);
                jwtSecretKey = jwtProperties.getFrontSecretKey();
                ttl = jwtProperties.getFrontTtl();
                break;
            default:
                return Result.error("无效的用户角色");
        }


        String token = JwtUtil.createJWT(jwtSecretKey, ttl, claims);

        UserLoginVO userLoginVO = UserLoginVO.builder()
                .id(getId(user))
                .username(getUsername(user))
                .name(getName(user))
                .token(token)
                .build();

        return Result.success(userLoginVO);
    }


    private Long getId(Object user) {
        if (user instanceof Admin) return ((Admin) user).getId();
        if (user instanceof Driver) return ((Driver) user).getId();
        if (user instanceof Mover) return ((Mover) user).getId();
        if (user instanceof Customer) return ((Customer) user).getId();
        return null;
    }

    private String getUsername(Object user) {
        if (user instanceof Admin) return ((Admin) user).getUsername();
        if (user instanceof Driver) return ((Driver) user).getUsername();
        if (user instanceof Mover) return ((Mover) user).getUsername();
        if (user instanceof Customer) return ((Customer) user).getUsername();
        return null;
    }

    private String getName(Object user) {
        if (user instanceof Admin) return ((Admin) user).getName();
        if (user instanceof Driver) return ((Driver) user).getName();
        if (user instanceof Mover) return ((Mover) user).getName();
        if (user instanceof Customer) return ((Customer) user).getName();
        return null;
    }

}