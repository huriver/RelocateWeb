package com.ahut.constant;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * 所有有效的订单状态值列表
     */
    public static final List<Integer> VALID_STATUSES = Arrays.asList(
            PENDING_ACCEPTANCE,
            DRIVER_ACCEPTED_WAITING_MOVERS,
            ACCEPTED,
            IN_PROGRESS,
            COMPLETED,
            CANCELLED
    );

    /**
     * 适用于司机、搬家工人端“我的订单”列表的状态值
     */
    public static final List<Integer> DRIVER_Mover_MY_ORDER_STATUSES = Arrays.asList(
            DRIVER_ACCEPTED_WAITING_MOVERS, // 1
            ACCEPTED, // 2
            IN_PROGRESS // 3
    );

    /**
     * 适用于搬家工人端“我的订单”列表的状态值
     */
    public static final List<Integer> MOVER_MY_ORDER_STATUSES = Arrays.asList(
            ACCEPTED, // 2
            IN_PROGRESS // 3
    );

    /**
     * 定义合法的状态流转规则
     * Map<当前状态, List<允许的目标状态>>
     * 这是根据我们之前的讨论确定的流转，反映了让主状态动态反映团队完整性的设计
     */
    public static final Map<Integer, List<Integer>> ALLOWED_TRANSITIONS_MAP = new HashMap<>();

    static {
        // 从 [待接单 (0)]
        ALLOWED_TRANSITIONS_MAP.put(PENDING_ACCEPTANCE, Arrays.asList(DRIVER_ACCEPTED_WAITING_MOVERS, CANCELLED));

        // 从 [司机已接单 (1)]
        // 可以回退到 待接单 (司机取消接单)
        // 可以推进到 已接单 (搬运工人数达标)
        // 可以到 已取消 (用户/管理员取消)
        ALLOWED_TRANSITIONS_MAP.put(DRIVER_ACCEPTED_WAITING_MOVERS, Arrays.asList(PENDING_ACCEPTANCE, ACCEPTED, CANCELLED));

        // 从 [已接单 (2)]
        // 可以回退到 司机已接单 (某个搬运工取消)
        // 可以推进到 进行中 (开始服务)
        // 可以到 已取消 (用户/管理员取消)
        ALLOWED_TRANSITIONS_MAP.put(ACCEPTED, Arrays.asList(DRIVER_ACCEPTED_WAITING_MOVERS, IN_PROGRESS, CANCELLED));

        // 从 [进行中 (3)]
        // 可以到 已完成 (完成服务)
        // 可以到 已取消 (管理员取消进行中订单)
        ALLOWED_TRANSITIONS_MAP.put(IN_PROGRESS, Arrays.asList(COMPLETED, CANCELLED));

        // [已完成 (4)] 和 [已取消 (5)] 是终态，不允许流转到其他状态。
        // 终态到自身的流转通常没有业务意义，由校验逻辑处理即可。
    }

    /**
     * 根据 int 值获取文字描述
     *
     * @param status 订单状态 int 值
     * @return 订单状态文字描述
     */
    public static String getDescription(Integer status) {
        switch (status) {
            case 0:
                return "待接单";
            case 1:
                return "司机已接单，等待搬运工人";
            case 2:
                return "已接单（团队已确认）";
            case 3:
                return "进行中";
            case 4:
                return "已完成";
            case 5:
                return "已取消";
            default:
                return "未知状态";
        }
    }

}