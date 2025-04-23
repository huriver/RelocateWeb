package com.***REMOVED***.interceptor;

import com.***REMOVED***.constant.JwtClaimsConstant;
import com.***REMOVED***.context.BaseContext;
import com.***REMOVED***.properties.JwtProperties;
import com.***REMOVED***.utils.JwtUtil;
import com.***REMOVED***.utils.RedisUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenBackInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisUtil redisUtil;

    /**
     * 校验jwt
     *
     * @param request
     * @param response
     * @param handler
     * @return
     * @throws Exception
     */
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //判断当前拦截到的是Controller的方法还是其他资源
        if (!(handler instanceof HandlerMethod)) {
            //当前拦截到的不是动态方法，直接放行
            return true;
        }

        String requestURI = request.getRequestURI();

        // 如果是消费者注册接口，直接放行
        if (requestURI.equals("/auth/register") && isCustomerRegistration(request)) {
            return true;
        }

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getBackTokenName());

        //2、校验令牌
        try {
            log.info("后端jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getBackSecretKey(), token); // 使用 backSecretKey
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.ID).toString()); // 使用 ID
            String role = claims.get(JwtClaimsConstant.ROLE).toString(); // 获取角色
            Date expiration = claims.getExpiration(); // 获取 Token 过期时间


            log.info("当前后端用户id：{}, 角色：{}", userId, role);

            // ====== 3. 检查 Token 是否在黑名单中 ======
            String blacklisted = redisUtil.get("jwt_blacklist:" + token);
            if (blacklisted != null) {
                log.warn("后端 Token {} 已在黑名单中，拒绝访问", token);
                response.setStatus(401); // 响应 401 未授权
                return false; // 拒绝访问
            }

            BaseContext.setCurrentId(userId);
            BaseContext.setCurrentUserRole(role);       // 存储角色
            BaseContext.setTokenExpiration(expiration); // 存储过期时间
            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
    }

    private boolean isCustomerRegistration(HttpServletRequest request) {
        String role = request.getHeader("role");
        return JwtClaimsConstant.ROLE_CUSTOMER.equals(role);
    }

    /**
     * 在请求完成后清理ThreadLocal中的数据
     *
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        BaseContext.remove();
    }


}
