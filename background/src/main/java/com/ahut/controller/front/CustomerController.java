package com.***REMOVED***.controller.front;

import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.ChangePasswordDTO;
import com.***REMOVED***.dto.CustomerDTO;
import com.***REMOVED***.entity.Customer;
import com.***REMOVED***.result.Result;
import com.***REMOVED***.service.CustomerService;
import com.***REMOVED***.vo.CustomerRatingVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 员工管理
 */
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

    /**
     * 获取当前用户历史评价列表
     *
     * @return
     */
    @GetMapping("/ratings")
    public Result<List<CustomerRatingVO>> getCustomerRatingHistory() {
        log.info("用户端用户{}，获取当前用户历史评价列表", BaseContext.getCurrentId());
        List<CustomerRatingVO> customerRatingVOList = customerService.getCustomerRatingHistory();
        return Result.success(customerRatingVOList);
    }

}
