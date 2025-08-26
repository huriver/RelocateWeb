package com.ahut.constant;

/**
 * 支付状态常量
 */
public class PaymentStatusConstant {

    /**
     * 是否支付：
     * 0-未支付
     * 1-已支付
     * 2-已退款
     */
    public static final Integer UN_PAID = 0; // 未支付
    public static final Integer PAID = 1; // 已支付
    public static final Integer REFUNDED = 2; // 已退款

    // 可以添加根据 int 值获取文字描述的方法
    public static String getDescription(Integer status) {
        switch (status) {
            case 0:
                return "未支付";
            case 1:
                return "已支付";
            case 2:
                return "已退款";
            default:
                return "未知状态";
        }
    }
}