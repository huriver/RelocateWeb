package com.***REMOVED***.constant;

/**
 * 支付方式常量
 */
public class PayMethodConstant {

    /**
     * 支付方式：
     * 1-微信支付
     * 2-支付宝
     * 3-云闪付
     * ... 其他支付方式
     */
    public static final Integer WECHAT_PAY = 1; // 微信支付
    public static final Integer ALIPAY = 2; // 支付宝
    public static final Integer UNIONPAY = 3; // 云闪付


    // 可以添加根据 int 值获取文字描述的方法，方便VO或日志封装
    public static String getDescription(Integer method) {
        if (method == null) return "未知支付方式";
        switch (method) {
            case 1:
                return "微信支付";
            case 2:
                return "支付宝";
            case 3:
                return "云闪付";
            default:
                return "未知支付方式";
        }
    }
}