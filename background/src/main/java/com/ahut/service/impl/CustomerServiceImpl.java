package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.Customer;
import com.***REMOVED***.exception.AccountLockedException;
import com.***REMOVED***.exception.AccountNotFoundException;
import com.***REMOVED***.exception.PasswordErrorException;
import com.***REMOVED***.mapper.CustomerMapper;
import com.***REMOVED***.mapper.RatingMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.CustomerService;
import com.***REMOVED***.vo.CustomerRatingVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RatingMapper ratingMapper;


    @Override
    public Customer login(UserLoginDTO userLoginDTO) {
        String username = userLoginDTO.getUsername();
        String password = userLoginDTO.getPassword();

        //1、根据用户名查询数据库中的数据
        Customer customer = customerMapper.getByUsername(username);

        //2、处理各种异常情况（用户名不存在、密码不对）
        if (customer == null) {
            //账号不存在
            throw new AccountNotFoundException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        //密码比对
        //后期需要进行md5加密，然后再进行比对
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        if (!password.equals(customer.getPassword())) {
            //密码错误
            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
        }

        //账号被封禁
        if (customer.getIsBanned()) {
            throw new AccountLockedException(MessageConstant.ACCOUNT_LOCKED);
        }

        //3、返回实体对象
        return customer;
    }

    @Override
    public void save(UserRegisterDTO userRegisterDTO) {
        Customer customer = new Customer();
        // 对象属性拷贝
        BeanUtils.copyProperties(userRegisterDTO, customer);
        // 对密码进行加密
        customer.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));
        customer.setName(customer.getUsername());
        customerMapper.insert(customer);
    }

    @Override
    public Customer getById(long id) {
        Customer customer = customerMapper.getById(id);
        customer.setPassword("****");
        return customer;
    }

    @Override
    public void update(CustomerDTO customerDTO) {
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerDTO, customer);
        customerMapper.update(customer);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 1. 从 BaseContext 获取当前登录用户的 ID
        Long currentCustomerId = BaseContext.getCurrentId();

        // 2. 从数据库中查询出当前登录用户的密码哈希值
        String storedPasswordHash = customerMapper.getById(currentCustomerId).getPassword();

        // 3. 验证旧密码是否正确
        String oldPasswordHashed = DigestUtils.md5DigestAsHex(changePasswordDTO.getOldPassword().getBytes());
        if (!oldPasswordHashed.equals(storedPasswordHash)) {
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }

        // 4. 校验新密码和确认新密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRePassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_NOT_MATCH);
        }

        // 6. 加密新密码
        String newPasswordHashed = DigestUtils.md5DigestAsHex(changePasswordDTO.getNewPassword().getBytes());

        // 7. 更新数据库中的密码
        Customer customer = Customer.builder()
                .id(currentCustomerId)
                .password(newPasswordHashed)
                .build();

        customerMapper.update(customer);
    }

    /**
     * 获取当前用户提交的历史评价记录列表
     *
     * @return
     */
    @Override
    public List<CustomerRatingVO> getCustomerRatingHistory() {
        return ratingMapper.getCustomerRatingByCustomerId(BaseContext.getCurrentId());
    }

    /**
     * 消费者分页查询
     *
     * @param pageQueryDTO
     * @return
     */
    @Override
    public PageResult pageQuery(CustomerPageQueryDTO pageQueryDTO) {
        PageHelper.startPage(pageQueryDTO.getPage(), pageQueryDTO.getPageSize());
        Page<Customer> page = customerMapper.pageQuery(pageQueryDTO);
        return new PageResult(page.getTotal(), page.getResult());
    }


}
