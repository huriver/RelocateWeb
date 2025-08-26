package com.ahut.service.impl;

import com.ahut.constant.MessageConstant;
import com.ahut.context.BaseContext;
import com.ahut.dto.*;
import com.ahut.entity.Customer;
import com.ahut.exception.AccountLockedException;
import com.ahut.exception.AccountNotFoundException;
import com.ahut.exception.BusinessException;
import com.ahut.exception.PasswordErrorException;
import com.ahut.mapper.CustomerMapper;
import com.ahut.mapper.OrderMapper;
import com.ahut.result.PageResult;
import com.ahut.service.CustomerService;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;

    @Autowired
    private OrderMapper orderMapper;

    @Value("${relocate.bcrypt.strength:10}") // 注入bcrypt强度，默认值为10
    private int bcryptStrength;

    // 定义一个静态 final 的 Pattern 对象，用于邮箱格式校验的正则表达式
    // 这是一个常用的基本邮箱格式正则表达式，可以根据需要调整
    private static final String EMAIL_REGEX = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,6}$";
    private static final Pattern EMAIL_PATTERN = Pattern.compile(EMAIL_REGEX);

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
//        password = DigestUtils.md5DigestAsHex(password.getBytes());
//        if (!password.equals(customer.getPassword())) {
//            //密码错误
//            throw new PasswordErrorException(MessageConstant.PASSWORD_ERROR);
//        }

        // 密码比对，使用 BCrypt.checkpw() 来验证明文密码和数据库中存储的哈希密码是否匹配
        if (!BCrypt.checkpw(password, customer.getPassword())) {
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
//        customer.setPassword(DigestUtils.md5DigestAsHex(userRegisterDTO.getPassword().getBytes()));

        // 5. 使用 BCrypt.hashpw() 加密新密码
        customer.setPassword(BCrypt.hashpw(userRegisterDTO.getPassword(), BCrypt.gensalt(bcryptStrength)));

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
    @Transactional
    public void update(CustomerDTO customerDTO) {
        // 1. 基本校验客户DTO
        if (customerDTO == null || customerDTO.getId() == null) {
            throw new BusinessException(MessageConstant.CUSTOMER_INFO_INCOMPLETE);
        }

        // 2. 校验 email 格式 (如果提供了 email)
        // 如果 DTO 中包含 email 字段且不为空
        if (customerDTO.getEmail() != null && !customerDTO.getEmail().isEmpty()) {
            // 使用正则表达式进行格式校验
            Matcher matcher = EMAIL_PATTERN.matcher(customerDTO.getEmail());
            if (!matcher.matches()) {
                // 如果格式不匹配，记录错误并抛出业务异常
                log.error("用户ID {} 提供的邮箱格式无效：{}", customerDTO.getId(), customerDTO.getEmail());
                throw new BusinessException(MessageConstant.CUSTOMER_EMAIL_INVALID);
            }
        }

        // 3. 复制 DTO 属性到实体并调用 Mapper 更新
        Customer updateCustomer = new Customer();
        BeanUtils.copyProperties(customerDTO, updateCustomer);

        // 调用 Mapper 更新数据库
        customerMapper.update(updateCustomer);
    }

    @Override
    public void changePassword(ChangePasswordDTO changePasswordDTO) {
        // 1. 从 BaseContext 获取当前登录用户的 ID
        Long currentCustomerId = BaseContext.getCurrentId();

        // 2. 从数据库中查询出当前登录用户的密码哈希值
        String storedPasswordHash = customerMapper.getById(currentCustomerId).getPassword();

        // 3. 验证旧密码是否正确
//        String oldPasswordHashed = DigestUtils.md5DigestAsHex(changePasswordDTO.getOldPassword().getBytes());
//        if (!oldPasswordHashed.equals(storedPasswordHash)) {
//            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
//        }

        // 3. 验证旧密码是否正确
        // 使用 BCrypt.checkpw() 验证旧密码
        if (!BCrypt.checkpw(changePasswordDTO.getOldPassword(), storedPasswordHash)) {
            throw new PasswordErrorException(MessageConstant.OLD_PASSWORD_ERROR);
        }

        // 4. 校验新密码和确认新密码是否一致
        if (!changePasswordDTO.getNewPassword().equals(changePasswordDTO.getRePassword())) {
            throw new PasswordErrorException(MessageConstant.PASSWORD_NOT_MATCH);
        }

//        // 5. 加密新密码
//        String newPasswordHashed = DigestUtils.md5DigestAsHex(changePasswordDTO.getNewPassword().getBytes());

        // 5. 加密新密码
        // 使用 BCrypt.hashpw() 加密新密码
        String newPasswordHashed = BCrypt.hashpw(changePasswordDTO.getNewPassword(), BCrypt.gensalt(bcryptStrength));


        // 6. 更新数据库中的密码
        Customer customer = Customer.builder()
                .id(currentCustomerId)
                .password(newPasswordHashed)
                .build();

        customerMapper.update(customer);
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
