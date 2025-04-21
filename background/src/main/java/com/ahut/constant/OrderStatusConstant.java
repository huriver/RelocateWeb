package com.***REMOVED***.constant;

/**
 * 订单状态常量
 */
public class OrderStatusConstant {

    /**
     * 订单状态：
     * 0-待接单
     * 1-司机已接单，等待搬运工人
     * 2-已接单 (司机和搬运工人团队已确认)
     * 3-进行中
     * 4-已完成
     * 5-已取消
     */
    public static final Integer PENDING_ACCEPTANCE = 0; // 待接单
    public static final Integer DRIVER_ACCEPTED_WAITING_MOVERS = 1; // 司机已接单，等待搬运工人
    public static final Integer ACCEPTED = 2; // 已接单 (团队确认)
    public static final Integer IN_PROGRESS = 3; // 进行中
    public static final Integer COMPLETED = 4; // 已完成
    public static final Integer CANCELLED = 5; // 已取消


    // 可以在这里添加一个根据 int 值获取文字描述的方法，方便日志或VO封装
    public static String getDescription(Integer status) {
        switch (status) {
            case 0:
                return "待接单";
            case 1:
                return "司机已接单";
            case 2:
                return "已接单";
            case 3:
                return "进行中";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            // case 6: return "已退款"; // 如果加了退款状态
            default:
                return "未知状态";
        }
    }
}