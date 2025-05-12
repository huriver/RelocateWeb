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