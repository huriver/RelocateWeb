// D:\Java\code\RelocateWeb\frontground\src\api\ratingApi.js

import axios from "@/utils/request.js";

// ========================= 前台用户端评价 API (保持不变) =========================

// 查询服务评价 (前台，用于服务详情页展示对服务的评价)
export const getServiceRatingApi = (serviceId) => {
  return axios.get(`/front/rating/service/${serviceId}`);
};

// 提交订单评价 (前台，用户对已完成订单进行评价)
export const submitOrderRatingApi = (data) => {
  return axios.post("/front/rating/review", data);
};

// 查询历史评价 (前台，用户查看自己的历史评价)
export const queryHistoryRatingsApi = () => {
  return axios.get("/front/rating/history");
};


// ========================= 后台管理端评价 API (保持不变) =========================

// 后台分页查询评价列表 (管理员查看所有评价)
// GET http://localhost:8080/back/rating/page
// params 包含: page, pageSize, ratingValueMin, ratingValueMax, orderNumber, ratingType, rateeName
export const getBackRatingListPageApi = (params) => {
  return axios.get("/back/rating/page", { params });
};

// 后台根据ID查询评价详情 (管理员查看评价详情)
// GET http://localhost:8080/back/rating/3
export const getBackRatingDetailApi = (id) => {
  return axios.get(`/back/rating/${id}`);
};


// ========================= 后台 司机 评价 API (新增) =========================

/**
 * 【后台司机】分页查询关于我的评价列表
 * GET /back/rating/driver/my-ratings
 * params 包含: page, pageSize, minRatingValue, maxRatingValue, startTime, endTime, orderNumber, customerName
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getDriverMyRatingsListApi = (params) => {
  return axios.get("/back/rating/driver/my-ratings", { params });
};

/**
 * 【后台司机】查询关于我的评价详情
 * GET /back/rating/driver/my-ratings/{id}
 * @param {number} id - 评价ID
 * @returns {Promise} axios promise
 */
export const getDriverMyRatingDetailApi = (id) => {
  return axios.get(`/back/rating/driver/my-ratings/${id}`);
};


// ========================= 后台 搬家工人 评价 API (新增) =========================

/**
 * 【后台搬家工人】分页查询关于我的评价列表
 * GET /back/rating/mover/my-ratings
 * params 包含: page, pageSize, minRatingValue, maxRatingValue, startTime, endTime, orderNumber, customerName
 * @param {object} params - 查询参数
 * @returns {Promise} axios promise
 */
export const getMoverMyRatingsListApi = (params) => {
  return axios.get("/back/rating/mover/my-ratings", { params });
};

/**
 * 【后台搬家工人】查询关于我的评价详情
 * GET /back/rating/mover/my-ratings/{id}
 * @param {number} id - 评价ID
 * @returns {Promise} axios promise
 */
export const getMoverMyRatingDetailApi = (id) => {
  return axios.get(`/back/rating/mover/my-ratings/${id}`);
};


// TODO: 如果有其他后台评价相关 API (如删除评价)，请在此处添加