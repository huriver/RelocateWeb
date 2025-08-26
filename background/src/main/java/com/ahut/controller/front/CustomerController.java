package com.ahut.controller.front;

import com.ahut.context.BaseContext;
import com.ahut.dto.ChangePasswordDTO;
import com.ahut.dto.CustomerDTO;
import com.ahut.entity.Customer;
import com.ahut.result.Result;
import com.ahut.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;


@RestController("frontCustomerController")
@RequestMapping("/front/customer")
@Slf4j
public class CustomerController {

    @Autowired
    private CustomerService customerService;

    /**
     * 根据id查询消费者信息
     *
     * @return
     */
    @GetMapping
    public Result<Customer> getById() {
        long id = BaseContext.getCurrentId();
        log.info("用户端根据id查询消费者信息:{}", id);
        Customer customer = customerService.getById(id);
        return Result.success(customer);
    }

    /**
     * 编辑消费者信息
     *
     * @param customerDTO
     * @return
     */
    @PutMapping
    public Result update(@RequestBody CustomerDTO customerDTO) {
        log.info("用户端编辑消费者信息:{}", customerDTO);
        customerService.update(customerDTO);
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
        log.info("用户端消费者修改密码：{}", changePasswordDTO);
        customerService.changePassword(changePasswordDTO);
        return Result.success();
    }

}
