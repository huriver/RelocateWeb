package com.***REMOVED***.service;

import com.***REMOVED***.constant.JwtClaimsConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.exception.BaseException;
import com.***REMOVED***.properties.JwtProperties;
import com.***REMOVED***.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Service
@Slf4j
public class AuthService {

    @Autowired
    private JwtProperties jwtProperties;
    @Autowired
    private RedisUtil redisUtil;

    /**
     * 用户端（消费者）注销逻辑
     *
     * @param request
     */
    public void customerLogout(HttpServletRequest request) {
        performLogout(request, JwtClaimsConstant.ROLE_CUSTOMER);
    }

    /**
     * 后端用户（管理员、司机、搬运工人）注销逻辑
     *
     * @param request
     */
    public void backendLogout(HttpServletRequest request) {
        performLogout(request, null);
    }


    /**
     * 执行核心注销逻辑 (私有方法)
     *
     * @param request
     * @param expectedRole
     */
    private void performLogout(HttpServletRequest request, String expectedRole) {
        // 1. 从 BaseContext 获取当前用户的 ID 和用户类型 (以及 Token 过期时间)
        Long userId = BaseContext.getCurrentId();
        String userRole = BaseContext.getCurrentUserRole();
        Date expiration = BaseContext.getTokenExpiration();

        // 检查 BaseContext 中是否有完整用户信息
        if (userId == null || !StringUtils.hasText(userRole) || expiration == null) {
            log.warn("尝试注销但 BaseContext 中没有完整用户信息，可能是未登录或认证失败的请求");
            throw new BaseException("用户未登录或认证信息无效");
        }

        // 检查用户角色是否符合期望 (如果指定了期望角色)
        if (expectedRole != null && !userRole.equals(expectedRole)) {
            log.warn("用户 {} (角色: {}) 尝试通过非预期接口注销 (期望角色: {})", userId, userRole, expectedRole);
            throw new BaseException("注销接口不匹配当前用户角色");
        }

        // 对于后端注销接口，只检查是否是已知的后端角色之一
        if (expectedRole == null && !(userRole.equals(JwtClaimsConstant.ROLE_ADMIN) ||
                userRole.equals(JwtClaimsConstant.ROLE_DRIVER) ||
                userRole.equals(JwtClaimsConstant.ROLE_MOVER))) {
            log.warn("用户 {} (角色: {}) 尝试通过后端注销接口注销，但角色不是管理员/司机/搬运工人", userId, userRole);
            throw new BaseException("后端注销接口不匹配当前用户角色");
        }


        // 2. 从请求头获取 Token 字符串
        String token = request.getHeader(jwtProperties.getFrontTokenName()); // 尝试获取前端 Token
        // 如果前端 Token 不存在 或 当前注销的是后端用户 (理论上后端用户不会携带前端 Token)
        if (!StringUtils.hasText(token) || (expectedRole != null && expectedRole != JwtClaimsConstant.ROLE_CUSTOMER) || (expectedRole == null && !userRole.equals(JwtClaimsConstant.ROLE_CUSTOMER))) {
            token = request.getHeader(jwtProperties.getBackTokenName()); // 尝试获取后端 Token
        }

        // 如果尝试从请求头未能获取到 Token
        if (!StringUtils.hasText(token)) {
            log.error("BaseContext 中有用户信息，但请求头中未找到 Token，用户ID: {}, 角色: {}", userId, userRole);
            throw new BaseException("无法获取用户的认证Token");
        }

        // 3. 将获取到的 Token 加入黑名单
        boolean addedToBlacklist = addTokenToBlacklist(token, expiration);

        if (addedToBlacklist) {
            log.info("用户 {} (角色: {}) 的 Token 已加入黑名单，注销成功", userId, userRole);
            // 注销成功后，清除 BaseContext 中的用户信息
            BaseContext.remove();
        } else {
            log.warn("用户 {} (角色: {}) 的 Token 加入黑名单失败", userId, userRole);
        }
    }

    /**
     * 将 Token 加入黑名单 (使用 Redis 模拟)
     * Token 失效时长使用 Token 的过期时间计算剩余有效期
     *
     * @param token      要加入黑名单的 Token
     * @param expiration Token 的过期时间
     * @return 是否成功加入黑名单
     */
    private boolean addTokenToBlacklist(String token, Date expiration) {
        try {
            // 计算 Token 的剩余有效期 (秒)
            long remainingSeconds = (expiration.getTime() - System.currentTimeMillis()) / 1000;

            // 如果 Token 已经过期或者剩余有效期 <= 0，给一个非常短的过期时间（例如 1 秒），确保即使已过期也加入黑名单
            if (remainingSeconds <= 0) {
                remainingSeconds = 1;
                log.warn("Token {} 已过期或剩余有效期 <= 0，将其加入黑名单，失效时长设置为 1 秒", token);
            }

            // Token 黑名单的 Key 加上前缀区分
            redisUtil.setEx("jwt_blacklist:" + token, "", remainingSeconds);
            log.debug("Token {} 已加入黑名单，失效时长 {} 秒", token, remainingSeconds);
            return true;
        } catch (Exception e) {
            log.error("将 Token {} 加入黑名单失败", token, e);
            return false;
        }
    }

}