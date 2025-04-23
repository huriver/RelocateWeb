package com.***REMOVED***.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Component
public class RedisUtil {

    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 设置指定 key 的值，并将 key 的过期时间设为指定的秒数。
     * 如果 key 已经存在，则 SETEX 命令会替换旧的值。
     *
     * @param key     键
     * @param value   值 (String 类型)
     * @param timeout 过期时间 (秒)
     * @return 成功返回 true，失败返回 false (StringRedisTemplate 的 set 方法通常不返回 boolean，这里包装一下)
     */
    public boolean setEx(String key, String value, long timeout) {
        try {
            stringRedisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
            return true; // 假设操作成功则返回 true
        } catch (Exception e) {
            // 记录异常信息
            // log.error("RedisUtil setEx error: key={}, value={}, timeout={}", key, value, timeout, e);
            return false; // 操作失败返回 false
        }
    }

    /**
     * 获取指定 key 的值。
     *
     * @param key 键
     * @return key 的值，如果 key 不存在，返回 null。
     */
    public String get(String key) {
        try {
            return stringRedisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            // 记录异常信息
            // log.error("RedisUtil get error: key={}", key, e);
            return null; // 操作失败返回 null
        }
    }

    /**
     * 删除指定的 key。
     *
     * @param key 键
     * @return 删除成功的 key 的数量。
     */
    public Boolean delete(String key) {
        try {
            return stringRedisTemplate.delete(key);
        } catch (Exception e) {
            // 记录异常信息
            // log.error("RedisUtil delete error: key={}", key, e);
            return false; // 操作失败返回 false
        }
    }

}