// D:\Java\code\RelocateWeb\frontground\src\api\adminDashboardApi.js

import axios from "@/utils/request.js"; // 明确命名为 axios

/**
 * 获取核心运营数据概览 (卡片展示数据)
 * GET /back/dashboard/businessData
 * @param {object} params - 查询参数 (可选: startDate, endDate)
 * @param {string} [params.startDate] - 统计开始日期 (yyyy-MM-dd)
 * @param {string} [params.endDate] - 统计结束日期 (yyyy-MM-dd)
 * @returns {Promise} axios promise
 */
export const getBusinessDataApi = (params) => {
  return axios.get("/back/dashboard/businessData", { params });
};

/**
 * 获取订单趋势数据
 * GET /back/dashboard/orderTrend
 * @param {object} params - 查询参数 (startDate, endDate, timeUnit)
 * @param {string} params.startDate - 统计开始日期 (yyyy-MM-dd)
 * @param {string} params.endDate - 统计结束日期 (yyyy-MM-dd)
 * @param {string} params.timeUnit - 时间粒度 ('DAY', 'WEEK', 'MONTH')
 * @returns {Promise} axios promise
 */
export const getOrderTrendApi = (params) => {
  return axios.get("/back/dashboard/orderTrend", { params });
};

/**
 * 获取营收趋势数据
 * GET /back/dashboard/revenueTrend
 * @param {object} params - 查询参数 (startDate, endDate, timeUnit)
 * @param {string} params.startDate - 统计开始日期 (yyyy-MM-dd)
 * @param {string} params.endDate - 统计结束日期 (yyyy-MM-dd)
 * @param {string} params.timeUnit - 时间粒度 ('DAY', 'WEEK', 'MONTH')
 * @returns {Promise} axios promise
 */
export const getRevenueTrendApi = (params) => {
  return axios.get("/back/dashboard/revenueTrend", { params });
};

/**
 * 获取用户增长趋势数据
 * GET /back/dashboard/userGrowthTrend
 * @param {object} params - 查询参数 (startDate, endDate, timeUnit, userType)
 * @param {string} params.startDate - 统计开始日期 (yyyy-MM-dd)
 * @param {string} params.endDate - 统计结束日期 (yyyy-MM-dd)
 * @param {string} params.timeUnit - 时间粒度 ('DAY', 'WEEK', 'MONTH')
 * @param {string} [params.userType] - 用户类型过滤 (可选, 'CUSTOMER', 'DRIVER', 'MOVER')
 * @returns {Promise} axios promise
 */
export const getUserGrowthTrendApi = (params) => {
  return axios.get("/back/dashboard/userGrowthTrend", { params });
};

/**
 * 获取订单状态分布数据
 * GET /back/dashboard/orderStatusDistribution
 * @param {object} params - 查询参数 (可选: startDate, endDate)
 * @param {string} [params.startDate] - 统计开始日期 (yyyy-MM-dd)
 * @param {string} [params.endDate] - 统计结束日期 (yyyy-MM-dd)
 * @returns {Promise} axios promise
 */
export const getOrderStatusDistributionApi = (params) => {
  return axios.get("/back/dashboard/orderStatusDistribution", { params });
};

/**
 * 获取服务/货车类型使用分布数据
 * GET /back/dashboard/serviceResourceDistribution
 * @param {object} params - 查询参数 (startDate, endDate, type)
 * @param {string} [params.startDate] - 统计开始日期 (yyyy-MM-dd)
 * @param {string} [params.endDate] - 统计结束日期 (yyyy-MM-dd)
 * @param {string} params.type - 资源类型 ('SERVICE' 或 'TRUCK_TYPE')
 * @returns {Promise} axios promise
 */
export const getServiceResourceDistributionApi = (params) => {
  return axios.get("/back/dashboard/serviceResourceDistribution", { params });
};

/**
 * 获取评分分布数据
 * GET /back/dashboard/ratingDistribution
 * @param {object} params - 查询参数 (可选: startDate, endDate, ratingType)
 * @param {string} [params.startDate] - 统计开始日期 (yyyy-MM-dd)
 * @param {string} [params.endDate] - 统计结束日期 (yyyy-MM-dd)
 * @param {string} [params.ratingType] - 评分类型过滤 (可选, 'DRIVER', 'MOVER', 'SERVICE')
 * @returns {Promise} axios promise
 */
export const getRatingDistributionApi = (params) => {
  return axios.get("/back/dashboard/ratingDistribution", { params });
};

/**
 * 获取评分靠前的司机列表
 * GET /back/dashboard/topDriversByRating
 * @param {object} params - 查询参数 (可选: limit, minRatingCount)
 * @param {number} [params.limit=10] - 返回数量限制
 * @param {number} [params.minRatingCount=10] - 最低评分次数要求
 * @returns {Promise} axios promise
 */
export const getTopDriversByRatingApi = (params) => {
  return axios.get("/back/dashboard/topDriversByRating", { params });
};

/**
 * 获取评分靠前的搬家工人列表
 * GET /back/dashboard/topMoversByRating
 * @param {object} params - 查询参数 (可选: limit, minRatingCount)
 * @param {number} [params.limit=10] - 返回数量限制
 * @param {number} [params.minRatingCount=10] - 最低评分次数要求
 * @returns {Promise} axios promise
 */
export const getTopMoversByRatingApi = (params) => {
  return axios.get("/back/dashboard/topMoversByRating", { params });
};

/**
 * 获取评分靠前的服务项列表
 * GET /back/dashboard/topServicesByRating
 * @param {object} params - 查询参数 (可选: limit, minRatingCount)
 * @param {number} [params.limit=10] - 返回数量限制
 * @param {number} [params.minRatingCount=10] - 最低评分次数要求
 * @returns {Promise} axios promise
 */
export const getTopServicesByRatingApi = (params) => {
  return axios.get("/back/dashboard/topServicesByRating", { params });
};