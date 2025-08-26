package com.ahut.exception;

/**
 * 服务项不存在异常
 */
public class ServiceNotFoundException extends BaseException {
    public ServiceNotFoundException() {
    }

    public ServiceNotFoundException(String msg) {
        super(msg);
    }
}