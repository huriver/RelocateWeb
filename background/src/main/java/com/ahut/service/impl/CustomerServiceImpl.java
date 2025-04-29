package com.***REMOVED***.service.impl;

import com.***REMOVED***.constant.MessageConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.dto.*;
import com.***REMOVED***.entity.Customer;
import com.***REMOVED***.exception.*;
import com.***REMOVED***.mapper.CustomerMapper;
import com.***REMOVED***.mapper.OrderMapper;
import com.***REMOVED***.mapper.RatingMapper;
import com.***REMOVED***.result.PageResult;
import com.***REMOVED***.service.CustomerService;
import com.***REMOVED***.vo.CustomerRatingVO;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.util.List;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private RatingMapper ratingMapper;

    @Autowired
    private OrderMapper orderMapper;


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

    /**
     * 更新消费者状态 (封禁/解封)
     *
     * @param id
     * @param isBanned
     */
    @Override
    public void updateStatus(Long id, Integer isBanned) {
        // 校验参数
        if (id == null || (isBanned != 0 && isBanned != 1)) {
            throw new BusinessException(MessageConstant.INVALID_PARAMETER);
        }

        // 查询消费者是否存在
        Customer customer = customerMapper.getById(id);
        if (customer == null) {
            throw new BusinessException(MessageConstant.ACCOUNT_NOT_FOUND);
        }

        // 1. 业务校验：从“正常”改为“封禁”时的特殊校验
        if (isBanned == 1) {
            log.info("消费者 {} 尝试从正常改为封禁，进行未完成订单校验", id);
            // 检查是否有未完成订单关联到该消费者ID，如果存在未完成订单，阻止封禁
            Integer pendingOrderCount = orderMapper.countPendingOrdersByCustomerId(id);
            if (pendingOrderCount != null && pendingOrderCount > 0) {
                log.error("封禁消费者 {} 失败：该消费者有 {} 个未完成订单，无法封禁", id, pendingOrderCount);
                throw new BusinessException(MessageConstant.CONSUMER_HAS_PENDING_ORDERS_BLOCKED_BAN);
            }
            log.info("消费者 {} 没有未完成订单，允许封禁", id);
        }

        // 2. 更新消费者状态
        Customer updateCustomer = Customer.builder()
                .id(id)
                .isBanned(isBanned == 1)
                .build();
        customerMapper.update(updateCustomer);
    }

}
