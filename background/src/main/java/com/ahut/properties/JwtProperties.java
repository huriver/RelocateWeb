package com.ahut.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "relocate.jwt")
@Data
public class JwtProperties {

    /**
     * 管理端员工生成jwt令牌相关配置
     */
    private String backSecretKey;
    private long backTtl;
    private String backTokenName;

    /**
     * 用户端用户生成jwt令牌相关配置
     */
    private String frontSecretKey;
    private long frontTtl;
    private String frontTokenName;

}
