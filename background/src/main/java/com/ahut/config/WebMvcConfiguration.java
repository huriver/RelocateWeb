package com.ahut.config;


import com.ahut.interceptor.JwtTokenBackInterceptor;
import com.ahut.interceptor.JwtTokenFrontInterceptor;
import com.ahut.json.JacksonObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import java.util.List;

/**
 * 配置类，注册web层相关组件
 */
@Configuration
@Slf4j
public class WebMvcConfiguration extends WebMvcConfigurationSupport {

    @Autowired
    private JwtTokenBackInterceptor jwtTokenBackInterceptor;

    @Autowired
    private JwtTokenFrontInterceptor jwtTokenFrontInterceptor;

    /**
     * 注册自定义拦截器
     *
     * @param registry
     */
    protected void addInterceptors(InterceptorRegistry registry) {
        log.info("开始注册自定义拦截器...");
        registry.addInterceptor(jwtTokenBackInterceptor)
                .addPathPatterns("/back/**") // 后台管理接口拦截
                .excludePathPatterns("/back/admin")
                .excludePathPatterns("/back/driver")// 放行司机注册接口 (POST 请求到 /back/driver)
                .excludePathPatterns("/back/mover");

        registry.addInterceptor(jwtTokenFrontInterceptor)
                .addPathPatterns("/front/**"); // 前端用户相关接口拦截 (假设你的前端接口都在 /front/** 下)
    }


    /**
     * 拓展Spring MVC框架的消息转换器,为了统一处理后端返回前端时间格式
     *
     * @param converters
     */
    @Override
    protected void extendMessageConverters(List<HttpMessageConverter<?>> converters) {
        log.info("扩展消息转换器...");

        //创建一个消息转换器对象
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();

        //需要为消息转换器设置一个对象转换器，对象转换器可以将Java对象序列化为json数据
        converter.setObjectMapper(new JacksonObjectMapper());

        //将自己的消息转化器加入容器中
        converters.add(0, converter);
    }

    /**
     * 配置静态资源处理器
     *
     * @param registry
     */
    @Override
    protected void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:D:/Java/code/RelocateWeb/background/uploads/images/");
    }

    /**
     * 跨域配置
     *
     * @param registry
     */
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:5173")
                .allowedMethods("GET", "POST", "PUT", "DELETE")
                .maxAge(3600)
                .allowedHeaders("Origin", "Accept", "Content-Type", "Authorization", "token")
                .allowCredentials(true);
    }
}
