// D:\Java\code\RelocateWeb\frontground\src\api\orderApi.js

import axios from "@/utils/request.js"; // 明确命名为 axios

// ========================= 前台用户端订单 API (保持不变) =========================

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

// 查询历史订单 (前台)
// 参数：page, pageSize, orderStatus (可选)
export const queryHistoryOrdersApi = (params) => {
  return axios.get("/front/order/historyOrders", { params });
};

// 查询订单详情 (前台)
// 参数：orderId (路径参数)
export const getFrontOrderDetailApi = (orderId) => {
  return axios.get(`/front/order/orderDetail/${orderId}`);
};

// 取消订单 (前台)
// 参数：orderId (路径参数), data: { cancelReason }
export const cancelFrontOrderApi = (orderId, data) => {
  return axios.put(`/front/order/cancel/${orderId}`, data);
};

// 获取订单状态列表 (前台)
export const getFrontOrderStatusApi = () => {
  return axios.get("/front/order/status");
};

/**
 * 提交订单评价 (前台)
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


// ========================= 后台管理端订单 API (原有) =========================

// 后台获取订单状态列表 (通用状态列表，如果司机/搬家工有专属接口，此接口可能仅管理员使用)
// GET http://localhost:8080/back/order/status
export const getBackOrderStatusApi = () => {
  return axios.get("/back/order/status");
};

// 后台获取支付状态列表
// GET http://localhost:8080/back/order/paymentStatus
export const getBackPaymentStatusApi = () => {
  return axios.get("/back/order/paymentStatus");
};


// 后台分页查询订单列表 (管理员使用)
// GET http://localhost:8080/back/order/page?page=1&pageSize=2&...
// params 包含: page, pageSize, orderNumber, orderStatus, isPaid, createTimeStart, createTimeEnd, customerKeyword, driverName, serviceItemName, categoryId, truckTypeId, reservationTimeStart, reservationTimeEnd
export const getBackOrderListPageApi = (params) => {
  return axios.get("/back/order/page", { params });
};

// 后台根据ID查询订单详情 (管理员使用)
// GET http://localhost:8080/back/order/orderDetail/2
export const getBackOrderDetailApi = (id) => {
  return axios.get(`/back/order/orderDetail/${id}`);
};

// 后台 管理员 取消订单
// PUT http://localhost:8080/back/order/cancel/5
// 需要在请求体中发送 { "cancelReason": "..." }
export const adminCancelOrderApi = (id, cancelReason) => {
  // 请求体是一个对象 { cancelReason: string }
  return axios.put(`/back/order/cancel/${id}`, { cancelReason });
};

// 后台管理员强制完成订单
// PUT http://localhost:8080/back/order/force-complete/17
export const forceCompleteOrderApi = (id) => {
  return axios.put(`/back/order/force-complete/${id}`);
};

// ========================= 后台 司机 订单 API (原有) =========================

/**
 * 【后台司机】分页查询待接订单列表
 * GET /back/order/driver/available-orders
 * params 包含: page, pageSize, startDate, endDate, truckTypeId, serviceCategoryId
 * @param {object} params - 查询参数 (startDate, endDate 对应预约时间范围)
 * @returns {Promise} axios promise
 */
export const getDriverAvailableOrdersApi = (params) => {
  return axios.get("/back/order/driver/available-orders", { params });
};

/**
 * 【后台司机】查询待接订单详情
 * GET /back/order/driver/available-orders/{id}
 * @param {number} id - 订单ID
 * @returns {Promise} axios promise
 */
export const getDriverAvailableOrderDetailApi = (id) => {
  return axios.get(`/back/order/driver/available-orders/${id}`);
};

/**
 * 【后台司机】接单
 * PUT /back/order/driver/available-orders/accept
 * data 包含: orderId
 * @param {object} data - 接单数据 (包含 orderId)
 * @returns {Promise} axios promise
 */
export const acceptDriverOrderApi = (data) => {
  return axios.put("/back/order/driver/available-orders/accept", data);
};


/**
 * 【后台司机】分页查询我的订单列表 (当前/进行中的订单)
 * GET /back/order/driver/my-orders
 * params 包含: page, pageSize, orderStatus, startDate, endDate, orderNumber
 * @param {object} params - 查询参数 (startDate, endDate 对应预约时间范围)
 * @returns {Promise} axios promise
 */
export const getDriverMyOrdersListApi = (params) => {
  return axios.get("/back/order/driver/my-orders", { params });
};

/**
 * 【后台司机】查询我的订单详情
 * GET /back/order/driver/my-orders/{id}
 * @param {number} id - 订单ID
 * @returns {Promise} axios promise
 */
export const getDriverMyOrderDetailApi = (id) => {
  return axios.get(`/back/order/driver/my-orders/${id}`);
};

/**
 * 【后台司机】取消订单
 * PUT /back/order/driver/my-orders/cancel
 * data 包含: orderId, cancelReason
 * @param {object} data - 取消订单数据 (包含 orderId, cancelReason)
 * @returns {Promise} axios promise
 */
export const cancelDriverOrderApi = (data) => {
  return axios.put("/back/order/driver/my-orders/cancel", data);
};

/**
 * 【后台司机】对订单开始搬运服务
 * PUT /back/order/driver/my-orders/start/{id}
 * @param {number} id - 订单ID
 * @returns {Promise} axios promise
 */
export const startDriverServiceApi = (id) => {
  return axios.put(`/back/order/driver/my-orders/start/${id}`);
};

/**
 * 【后台司机】对订单完成搬运服务
 * PUT /back/order/driver/my-orders/complete/{id}
 * @param {number} id - 订单ID
 * @returns {Promise} axios promise
 */
export const completeDriverServiceApi = (id) => {
  return axios.put(`/back/order/driver/my-orders/complete/${id}`);
};


/**
 * 【后台司机/搬家工人】获取我的订单筛选订单状态列表 (返回司机已接单，等待搬运工人、已接单（团队已确认）、进行中状态)
 * GET /back/order/driver-mover/my-orders/statuses
 * @returns {Promise} axios promise
 */
export const getDriverMoverMyOrdersStatusesApi = () => {
  return axios.get("/back/order/driver-mover/my-orders/statuses");
};

// ========================= 后台 司机/搬家工人 历史订单 API (新增) =========================

/**
 * 【后台司机/搬家工人】获取历史订单筛选订单状态列表 (返回已完成、已取消状态)
 * GET /back/order/historical-orders/statuses
 * @returns {Promise} axios promise
 */
export const getDriverMoverHistoricalOrdersStatusesApi = () => {
  return axios.get("/back/order/historical-orders/statuses");
};

/**
 * 【后台司机】分页查询历史订单列表
 * GET /back/order/driver/historical-orders
 * params 包含: page, pageSize, orderNumber, customerName, beginEndTime, endEndTime, orderStatus
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getDriverHistoricalOrdersListApi = (params) => {
  return axios.get("/back/order/driver/historical-orders", { params });
};

/**
 * 【后台司机】查询历史订单详情
 * GET /back/order/driver/historical-orders/{id}
 * @param {number} id - 订单ID
 * @returns {Promise} axios promise
 */
export const getDriverHistoricalOrderDetailApi = (id) => {
  return axios.get(`/back/order/driver/historical-orders/${id}`);
};


// ========================= 后台 搬家工人 订单 API (新增) =========================

/**
 * 【后台搬家工人】分页查询待接订单列表
 * GET /back/order/mover/available-orders
 * params 包含: page, pageSize, startDate, endDate, numberOfHelpers, serviceCategoryId, truckTypeId
 * @param {object} params - 查询参数 (startDate, endDate 对应预约时间范围)
 * @returns {Promise} axios promise
 */
export const getMoverAvailableOrdersApi = (params) => {
  // 根据您提供的 API 文档，mover 的待接单接口路径是 /back/order/mover/available-orders
  return axios.get("/back/order/mover/available-orders", { params });
};

/**
 * 【后台搬家工人】查询待接订单详情
 * GET /back/order/mover/available-orders/{id}
 * @param {number} id - 订单ID
 * @returns {Promise} axios promise
 */
export const getMoverAvailableOrderDetailApi = (id) => {
  // 根据您提供的 API 文档，mover 的待接单详情接口路径是 /back/order/mover/available-orders/{id}
  return axios.get(`/back/order/mover/available-orders/${id}`);
};

/**
 * 【后台搬家工人】接单
 * PUT /back/order/mover/available-orders/accept
 * data 包含: orderId
 * @param {object} data - 接单数据 (包含 orderId)
 * @returns {Promise} axios promise
 */
export const acceptMoverOrderApi = (data) => {
  // 根据您提供的 API 文档，mover 的接单接口路径是 /back/order/mover/available-orders/accept
  return axios.put("/back/order/mover/available-orders/accept", data);
};

/**
 * 【后台搬家工人】分页查询我的订单列表 (当前/进行中的订单)
 * GET /back/order/mover/my-orders
 * params 包含: page, pageSize, orderStatus, startDate, endDate, orderNumber
 * @param {object} params - 查询参数 (startDate, endDate 对应预约时间范围)
 * @returns {Promise} axios promise
 */
export const getMoverMyOrdersListApi = (params) => {
  return axios.get("/back/order/mover/my-orders", { params });
};

/**
 * 【后台搬家工人】查询我的订单详情
 * GET /back/order/mover/my-orders/{id}
 * @param {number} id - 订单ID
 * @returns {Promise} axios promise
 */
export const getMoverMyOrderDetailApi = (id) => {
  return axios.get(`/back/order/mover/my-orders/${id}`);
};

/**
 * 【后台搬家工人】取消订单
 * PUT /back/order/mover/my-orders/cancel
 * data 包含: orderId, cancelReason
 * @param {object} data - 取消订单数据 (包含 orderId, cancelReason)
 * @returns {Promise} axios promise
 */
export const cancelMoverOrderApi = (data) => {
  return axios.put("/back/order/mover/my-orders/cancel", data);
};

/**
 * 【后台搬家工人】分页查询历史订单列表
 * GET /back/order/mover/historical-orders // <-- 这个是列表接口
 * params 包含: page, pageSize, orderNumber, customerName, beginEndTime, endEndTime, orderStatus
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getMoverHistoricalOrdersListApi = (params) => {
  // 调用对应的搬家工人历史订单列表接口
  return axios.get("/back/order/mover/historical-orders", { params });
};

/**
 * 【后台搬家工人】查询历史订单详情
 * GET /back/order/mover/historical-orders/{id} // <-- 这个是详情接口
 * @param {number} id - 订单ID
 * @returns {Promise} axios promise
 */
export const getMoverHistoricalOrderDetailApi = (id) => {
  // 调用对应的搬家工人历史订单详情接口
  return axios.get(`/back/order/mover/historical-orders/${id}`);
};