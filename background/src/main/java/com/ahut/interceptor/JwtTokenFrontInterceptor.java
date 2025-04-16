package com.ahut.interceptor;

import com.ahut.constant.JwtClaimsConstant;
import com.ahut.context.BaseContext;
import com.ahut.properties.JwtProperties;
import com.ahut.utils.JwtUtil;
import io.jsonwebtoken.Claims;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * jwt令牌校验的拦截器
 */
@Component
@Slf4j
public class JwtTokenFrontInterceptor implements HandlerInterceptor {

    @Autowired
    private JwtProperties jwtProperties;

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

        //1、从请求头中获取令牌
        String token = request.getHeader(jwtProperties.getFrontTokenName());

        //2、校验令牌
        try {
            log.info("前端jwt校验:{}", token);
            Claims claims = JwtUtil.parseJWT(jwtProperties.getFrontSecretKey(), token);
            Long userId = Long.valueOf(claims.get(JwtClaimsConstant.ID).toString()); // 使用 ID
            String role = claims.get(JwtClaimsConstant.ROLE).toString(); // 获取角色

            log.info("当前前端用户id：{}, 角色：{}", userId, role);

            BaseContext.setCurrentId(userId);
            BaseContext.setCurrentUserRole(role); // 存储角色

            //3、通过，放行
            return true;
        } catch (Exception ex) {
            //4、不通过，响应401状态码
            response.setStatus(401);
            return false;
        }
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
