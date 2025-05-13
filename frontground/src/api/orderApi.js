// src/api/order.js

import axios from "@/utils/request.js"; // 确保这里的路径和变量名与您的实际情况一致

// 订单价格估算
export const estimateOrderPriceApi = (data) => {
  return axios.post("/front/order/estimate", data);
};

// 用户下单
export const submitOrderApi = (data) => {
  return axios.post("/front/order/submit", data);
};

// 订单支付
export const orderPaymentApi = (data) => {
  return axios.put("/front/order/payment", data); // 注意这里是 PUT 请求
};

// --- 新增个人中心相关订单API ---

// 查询历史订单
// 参数：page, pageSize, orderStatus (可选)
export const queryHistoryOrdersApi = (params) => {
  return axios.get("/front/order/historyOrders", { params });
};

// 查询订单详情
// 参数：orderId (路径参数)
export const getOrderDetailApi = (orderId) => {
  return axios.get(`/front/order/orderDetail/${orderId}`);
};

// 取消订单
// 参数：orderId (路径参数), data: { cancelReason }
export const cancelOrderApi = (orderId, data) => {
  return axios.put(`/front/order/cancel/${orderId}`, data);
};

// 获取订单状态列表
export const getOrderStatusApi = () => {
  return axios.get("/front/order/status");
};

/**
 * 提交订单评价
 * @param {object} payload - 评价数据
 * @param {number} payload.orderId - 订单ID
 * @param {Array<object>} payload.ratings - 评价列表
 * @param {number} payload.ratings[].rateeId - 被评价对象ID (司机ID, 搬运工ID, 或服务ID)
 * @param {string} payload.ratings[].ratingType - 评价类型 ("DRIVER", "MOVER", "SERVICE")
 * @param {number} payload.ratings[].ratingValue - 星级评分 (1-5)
 * @param {string} [payload.ratings[].comment] - 评价内容 (可选)
 */
export const submitOrderRatingApi = (payload) => {
  return axios.post("/front/rating/review", payload); // 更新为新的评价接口路径和方法
};