package com.***REMOVED***.constant;

/**
 * 信息提示常量类
 */
public class MessageConstant {

    public static final String PASSWORD_ERROR = "密码错误";
    public static final String ACCOUNT_NOT_FOUND = "账号不存在";
    public static final String ACCOUNT_LOCKED = "账号被封禁";
    public static final String ALREADY_EXISTS = "已存在";
    public static final String UNKNOWN_ERROR = "未知错误";
    public static final String USER_NOT_LOGIN = "用户未登录";
    public static final String CATEGORY_BE_RELATED_BY_SETMEAL = "当前分类关联了套餐,不能删除";
    public static final String CATEGORY_BE_RELATED_BY_DISH = "当前分类关联了菜品,不能删除";
    public static final String SHOPPING_CART_IS_NULL = "购物车数据为空，不能下单";
    public static final String ADDRESS_BOOK_IS_NULL = "用户地址为空，不能下单";
    public static final String LOGIN_FAILED = "登录失败";
    public static final String UPLOAD_FAILED = "文件上传失败";
    public static final String SETMEAL_ENABLE_FAILED = "套餐内包含未启售菜品，无法启售";
    public static final String PASSWORD_EDIT_FAILED = "密码修改失败";
    public static final String DISH_ON_SALE = "起售中的菜品不能删除";
    public static final String SETMEAL_ON_SALE = "起售中的套餐不能删除";
    public static final String DISH_BE_RELATED_BY_SETMEAL = "当前菜品关联了套餐,不能删除";
    public static final String ORDER_STATUS_ERROR = "订单状态错误";
    public static final String ORDER_NOT_FOUND = "订单不存在";


    public static final String OLD_PASSWORD_ERROR = "旧密码错误";
    public static final String PASSWORD_NOT_MATCH = "两次密码不一致";
    public static final String SYSTEM_MOVER_FEE_CONFIG_MISSING = "系统搬运工人费用配置缺失";
    public static final String SYSTEM_MOVER_FEE_CONFIG_INVALID_VALUE = "系统搬运工人费用配置值无效";

    public static final String SERVICE_ITEM_OR_ADDRESS_EMPTY = "服务项、起止地址不能为空";
    public static final String PER_HELPER_FEE_LABEL = "per_helper_cost";
    public static final String SYSTEM_MOVER_FEE_CONFIG_INVALID_VALUE_WHILE_ESTIMATING_PRICE = "系统搬运工人费用配置值无效，无法估算价格";

    public static final String SERVICE_ITEM_NOT_EXIST = "服务项不存在，无法估算价格";
    public static final String TRUCK_TYPE_NOT_EXIST = "货车类型不存在，无法估算价格";
    public static final String SERVICE_CATEGORY_NOT_EXIST = "服务分类不存在，无法估算价格";

    public static final String UNKNOWN_ERROR_WHILE_CALCULATING_MOVING_DISTANCE = "计算搬家距离发生未知错误";

    public static final String ORDER_INFO_INCOMPLETE = "订单信息不完整";
    public static final String RESERVATION_TIME_INVALID = "预约时间无效";

    public static final String PAYMENT_INFO_INCOMPLETE = "支付信息不完整";
    public static final String ORDER_NOT_FOUND_WHILE_PAY_SUCCESS = "支付成功，订单不存在";
    public static final String UNKNOWN_ERROR_WHILE_MOCK_PAYMENT = "模拟支付发生未知错误";
    public static final String UPDATE_ORDER_PAY_STATUS_FAILED = "更新订单支付状态失败";

    public static final String ORDER_NOT_BELONG_TO_CURRENT_USER = "订单不属于当前用户";
    public static final String ORDER_STATUS_NOT_ALLOW_CANCEL = "订单状态不允许取消";

    public static final String REVIEW_INFO_INCOMPLETE = "评价信息不完整";
    public static final String ORDER_STATUS_NOT_ALLOW_REVIEW = "订单状态不允许评价";
    public static final String INVALID_REVIEW_SCORE = "无效的评分";
    public static final String ORDER_ALREADY_REVIEWED = "订单已评价";
    public static final String INVALID_RATING_TARGET = "无效的评分对象";
    public static final String INVALID_RATING_TYPE = "无效的评分类型";

    public static final String SERVICE_CATEGORY_BE_RELATED_BY_SERVICE = "当前服务分类关联了服务,不能删除";
    public static final String TRUCK_TYPE_BE_RELATED_BY_DRIVER = "当前货车类型关联了司机,不能删除";
    public static final String TRUCK_TYPE_BE_RELATED_BY_VEHICLE = "当前货车类型关联了车辆,不能删除";
    public static final String TRUCK_TYPE_BE_RELATED_BY_SERVICE = "当前货车类型关联了服务,不能删除";
    public static final String TRUCK_TYPE_BE_RELATED_BY_ORDER = "当前货车类型关联了订单,不能删除";

    public static final String DRIVER_NOT_FOUND = "司机不存在";
    public static final String TRUCK_TYPE_NOT_FOUND = "货车类型不存在";
    public static final String LICENSE_PLATE_NUMBER_EXIST = "车牌号已存在";
    public static final String DRIVER_TRUCK_TYPE_VEHICLE_EXIST = "司机在对应货车类型下已有一辆车";
    public static final String VEHICLE_BE_RELATED_BY_ORDER = "当前车辆关联了订单,不能删除";

    public static final String DRIVER_TRUCK_TYPE_EXIST = "司机和货车类型关联已存在";
    public static final String INVALID_PARAMETER = "参数错误";
    public static final String INVALID_TRUCK_TYPE = "无效的货车类型";
    public static final String DRIVER_TRUCK_TYPE_HAS_PENDING_ORDER = "司机的对应货车类型有未完成订单，无法删除";
    public static final String DRIVER_TRUCK_TYPE_HAS_ASSIGNED_VEHICLE = "司机的对应货车类型已分配车辆，无法删除";
    public static final String SERVICE_CATEGORY_NOT_FOUND = "服务分类不存在";
    public static final String SERVICE_NAME_ALREADY_EXISTS_IN_CATEGORY = "服务名称在服务分类下已存在";
    public static final String SERVICE_NOT_FOUND = "服务项不存在";
    public static final String SERVICE_ACTIVE_STRUCTURAL_CHANGE_BLOCKED = "服务项状态为启用时，不允许进行结构化修改";
    public static final String SERVICE_HAS_PENDING_ORDERS_STRUCTURAL_CHANGE_BLOCKED = "服务项有未完成订单时，不允许进行结构化修改";

    public static final String SERVICE_HAS_ASSOCIATED_ORDERS = "当前服务项关联了订单,无法删除";
    public static final String SERVICE_HAS_ASSOCIATED_RATINGS = "当前服务项关联了评价,无法删除";
    public static final String SERVICE_INFO_INCOMPLETE_FOR_ACTIVATION = "服务信息不完整，无法启用";
    public static final String SERVICE_HAS_PENDING_ORDERS_BLOCKED_ACTIVATION = "服务项有未完成订单时，不允许启用";
    public static final String CONSUMER_HAS_PENDING_ORDERS_BLOCKED_BAN = "用户有未完成订单时，不允许禁用";

    public static final String DRIVER_HAS_PENDING_ORDERS_BLOCKED_BAN = "司机有未完成订单时，不允许禁用";
    public static String Mover_HAS_PENDING_ORDERS_BLOCKED_BAN = "搬运工人有未完成订单时，不允许禁用";

    public static final String CANNOT_MODIFY_SELF_STATUS = "不能修改自己状态";
    public static final String CANNOT_RESET_SELF_PASSWORD = "不能重置自己密码";

    public static final String RATING_NOT_FOUND = "评分记录不存在";
    public static final String ORDER_STATUS_NOT_ALLOW_FORCE_COMPLETE = "订单当前状态不允许强制完成";
}
